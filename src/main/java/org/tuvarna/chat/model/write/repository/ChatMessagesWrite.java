package org.tuvarna.chat.model.write.repository;

import jakarta.data.repository.*;
import jakarta.persistence.PersistenceException;
import org.hibernate.StatelessSession;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;
import org.tuvarna.chat.model.write.dto.MessagePersistenceStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessagesWrite {

    Logger log = LoggerFactory.getLogger(ChatMessagesWrite.class);

    StatelessSession session();
    default MessagePersistenceStatus insertMessages(List<ChatMessageOperationalData> dataList) {

        if (dataList == null || dataList.isEmpty()) {
            return new MessagePersistenceStatus(false, new boolean[0]);
        }

        StatelessSession session = session();
        Instant currentTime = Instant.now();

        boolean failedMessages = false;
        boolean[] errorIndexes = new boolean[dataList.size()];

        final String insertSql = """
        INSERT INTO chat_message
            (id,
             chatroom_id,
             client_message_id,
             sender_user_id,
             content,
             time_sent,
             deleted)
        SELECT
            nextval('seq_chat_message'),
            :chatroomId,
            :clientMessageId,
            cu.id,
            :content,
            :timeSent,
            false
        FROM chatroom_user cu
        WHERE cu.chatroom_id = :chatroomId
          AND cu.user_id = :senderUserId
        LIMIT 1;
        
        """;

        int index = 0;

        for (ChatMessageOperationalData d : dataList) {

            try {
                int rows = session.createNativeQuery(insertSql, ChatMessage.class)
                        .setParameter("chatroomId", d.chatroomId())
                        .setParameter("clientMessageId", UUID.fromString(d.clientMessageId()))

                        .setParameter("senderUserId", d.senderUserId())
                        .setParameter("content", d.content())
                        .setParameter("timeSent", currentTime)
                        .executeUpdate();

                if (rows == 0) {
                    log.error(
                            "Sender validation failed at index {}: no ChatroomUser with user_id={} in chatroom_id={} with id={}",
                            index, d.senderUserId(), d.chatroomId(), d.senderId()
                    );
                    failedMessages = true;
                    errorIndexes[index] = true;
                }

            } catch (ConstraintViolationException e) {
                log.error("Constraint violation at index {}, dto: {}", index, d, e);
                failedMessages = true;
                errorIndexes[index] = true;

            } catch (PersistenceException e) {
                log.error("Persistence error at index {}, dto: {}", index, d, e);
                failedMessages = true;
                errorIndexes[index] = true;

            } catch (Exception e) {
                log.error("Unexpected error at index {}, dto: {}", index, d, e);
                failedMessages = true;
                errorIndexes[index] = true;
            }

            index++;
        }

        return new MessagePersistenceStatus(failedMessages, errorIndexes);
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
