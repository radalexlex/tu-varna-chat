package org.tuvarna.chat.model.read.repository.projection;

import jakarta.data.repository.Repository;
import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
import org.jspecify.annotations.NonNull;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface ChatroomEventfulRead {

    int PAGE_SIZE = 50;

    @NonNull
    StatelessSession session();

    default ContentPage<ChatroomEventfulElement> findPageChatroomEventful(
            long userId,
            Instant lastActivity,
            Integer lastChatroomId,
            Long lastChatMessageId) {

        String sql =
                """
                        SELECT cu.user_id,
                               cu.last_read,
                               cr.id AS cr_id,
                               cr.name,
                               cm.content,
                               cm.client_message_id,
                               cm.id AS cm_id,
                               COALESCE(cm.time_sent, cr.created_at) AS activity_time
                        FROM chatroom_user user_cu
                        JOIN chatroom cr ON cr.id = user_cu.chatroom_id
                        LEFT JOIN LATERAL (
                            SELECT cm.content,
                                   cm.time_sent,
                                   cm.client_message_id,
                                   cm.sender_user_id,
                                   cm.id
                            FROM chat_message cm
                            WHERE cm.chatroom_id = cr.id
                            ORDER BY cm.time_sent DESC, cm.id DESC
                            LIMIT 1
                        ) cm ON true
                        LEFT JOIN chatroom_user cu ON cu.id = cm.sender_user_id
                        WHERE user_cu.user_id = :userId
                          AND (
                                :lastActivity IS NULL
                             OR (
                                    COALESCE(cm.time_sent, cr.created_at) < :lastActivity
                                 OR (
                                        COALESCE(cm.time_sent, cr.created_at) = :lastActivity
                                    AND cr.id > :lastChatroomId
                                    )
                                 OR (
                                        COALESCE(cm.time_sent, cr.created_at) = :lastActivity
                                    AND cr.id = :lastChatroomId
                                    AND COALESCE(cm.id, 0) > :lastChatMessageId
                                    )
                                )
                              )
                        ORDER BY COALESCE(cm.time_sent, cr.created_at) DESC,
                                 cr.id ASC,
                                 COALESCE(cm.id, 0) ASC
                        LIMIT :limit + 1
                        """;

        List<ChatroomEventfulElement> resultList = new ArrayList<>();

        try (StatelessSession session = session()) {

            NativeQuery<ChatroomEventfulElement> query =
                    session.createNativeQuery(sql, ChatroomEventfulElement.class);

            query.setParameter("userId", userId);
            query.setParameter("lastActivity", lastActivity);
            query.setParameter("lastChatroomId", lastChatroomId);
            query.setParameter("lastChatMessageId", lastChatMessageId);
            query.setParameter("limit", PAGE_SIZE);

            resultList = query.getResultList();
        }

        if (resultList.size() > PAGE_SIZE) {
            resultList.removeLast();
            return new ContentPage<>(resultList, true);
        } else {
            return new ContentPage<>(resultList, false);
        }
    }
}



