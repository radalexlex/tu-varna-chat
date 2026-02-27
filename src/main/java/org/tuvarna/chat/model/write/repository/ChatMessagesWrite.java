package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.*;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.write.dto.ChatMessageSaveData;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessagesWrite {

    StatelessSession session();

    default int insertMessages(List<ChatMessageSaveData> dataList) {

        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }

        StatelessSession session = session();

        int count = 0;

        for (ChatMessageSaveData d : dataList) {

            ChatMessage entity = new ChatMessage();
            entity.setChatroomId(d.chatroomId());
            entity.setClientMessageId(UUID.fromString(d.clientMessageId()));
            entity.setSenderUserId(d.senderId());
            entity.setContent(d.content());
            entity.setTimeSent(Instant.now());
            entity.setDeleted(false);

            session.insert(entity);
            count++;
        }

        return count;

    }

    @Save
    ChatMessage save(ChatMessage entity);

    @Save
    List<ChatMessage> saveAll(List<ChatMessage> entities);

    @Query("update ChatMessage cm set cm.content = :newContent where cm.id = :messageId")
    @Update
    int updateMessageById(@Param("messageId") long messageId,
                          @Param("newContent") String content); // returns number of changed rows

    @Query("update ChatMessage cm set cm.deleted = true where cm.id = :messageId")
    @Update
    int archiveMessageById(@Param("messageId") long messageId); // returns number of changed rows

}


//    DataSource getDataSource();
//
//    @Query("update ChatMessage cm set cm.deleted = true")
//    @Update
//    ChatMessage insert();
//
//    @Insert
