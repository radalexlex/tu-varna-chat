package org.tuvarna.chat.model.read.repository.projection;

import jakarta.data.repository.Repository;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatroomEventfulRead {

    int PAGE_SIZE = 50;

    Connection connection();

    default List<ChatroomEventfulElement> findPageChatroomEventful(
            long userId,
            Instant lastActivity,
            Integer lastChatroomId,
            Long lastChatMessageId) {

        String query =
                "SELECT cu.user_id, cr.id AS cr_id, cr.name, " +
                        "       cm.content, cm.client_message_id, cm.id AS cm_id, " +
                        "       COALESCE(cm.time_sent, cr.created_at) AS activity_time " +
                        "FROM chatroom_user user_cu " +
                        "JOIN chatroom cr ON cr.id = user_cu.chatroom_id " +
                        "LEFT JOIN LATERAL ( " +
                        "    SELECT cm.content, cm.time_sent, cm.client_message_id, " +
                        "           cm.sender_user_id, cm.id " +
                        "    FROM chat_message cm " +
                        "    WHERE cm.chatroom_id = cr.id " +
                        "    ORDER BY cm.time_sent DESC, cm.id DESC " +
                        "    LIMIT 1 " +
                        ") cm ON true " +
                        "LEFT JOIN chatroom_user cu ON cu.id = cm.sender_user_id " +

                        "WHERE user_cu.user_id = ? " +

                        "AND ( ? IS NULL " +
                        "   OR ( COALESCE(cm.time_sent, cr.created_at) < ? " +
                        "     OR (COALESCE(cm.time_sent, cr.created_at) = ? AND cr.id > ?) " +
                        "     OR (COALESCE(cm.time_sent, cr.created_at) = ? AND cr.id = ? AND COALESCE(cm.id, 0) > ?) " +
                        "   ) " +
                        ") " +
                        "ORDER BY COALESCE(cm.time_sent, cr.created_at) DESC, " +
                        "         cr.id ASC, COALESCE(cm.id, 0) ASC " +
                        "LIMIT ?";

        List<ChatroomEventfulElement> result = new ArrayList<>();

        try (PreparedStatement stmt = connection().prepareStatement(query)) {

            int i = 1;

            stmt.setLong(i++, userId);

            if (lastActivity != null) {
                stmt.setTimestamp(i++, Timestamp.from(lastActivity));
                stmt.setTimestamp(i++, Timestamp.from(lastActivity));
                stmt.setTimestamp(i++, Timestamp.from(lastActivity));
                stmt.setInt(i++, lastChatroomId);
                stmt.setTimestamp(i++, Timestamp.from(lastActivity));
            } else {
                stmt.setNull(i++, Types.TIMESTAMP);
                stmt.setNull(i++, Types.TIMESTAMP);
                stmt.setNull(i++, Types.TIMESTAMP);
                stmt.setInt(i++, lastChatroomId);
                stmt.setNull(i++, Types.TIMESTAMP);
            }
            stmt.setInt(i++, lastChatroomId);
            stmt.setLong(i++, lastChatMessageId);

            stmt.setInt(i, PAGE_SIZE);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new ChatroomEventfulElement(
                            rs.getLong("user_id"),
                            rs.getInt("cr_id"),
                            rs.getString("name"),
                            rs.getString("content"),
                            rs.getLong("cm_id"),
                            rs.getObject("client_message_id", UUID.class).toString(),
                            rs.getTimestamp("activity_time").toInstant().toString()
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch chatroom messages", e);
        }

        return result;
    }
}



