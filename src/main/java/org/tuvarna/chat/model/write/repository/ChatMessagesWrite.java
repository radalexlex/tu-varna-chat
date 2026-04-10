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

        int count = 0;
        boolean failedMessages = false;
        boolean[] errorIndexes = new boolean[dataList.size()];

        for (ChatMessageOperationalData d : dataList) {

            ChatMessage entity = new ChatMessage();
            entity.setChatroomId(d.chatroomId());
            entity.setClientMessageId(UUID.fromString(d.clientMessageId()));
            entity.setSenderUserId(d.senderId());
            entity.setContent(d.content());
            entity.setTimeSent(currentTime);
            entity.setDeleted(false);

            try {
                session.insert(entity);


            } catch (ConstraintViolationException e) {
                log.error("Constraint violation at index {}, entity: {}", count, entity, e);

                failedMessages = true;
                errorIndexes[count] = true;

            } catch (PersistenceException e) {
                log.error("Persistence error at index {}, entity: {}", count, entity, e);

                failedMessages = true;
                errorIndexes[count] = true;

            } catch (Exception e) {
                log.error("Unexpected error at index {}, entity: {}", count, entity, e);

                failedMessages = true;
                errorIndexes[count] = true;
            }

            count++;
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
