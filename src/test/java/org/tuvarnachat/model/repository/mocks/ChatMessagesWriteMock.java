package org.tuvarnachat.model.repository.mocks;

import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.StatelessSession;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;
import org.tuvarna.chat.model.write.dto.MessagePersistenceStatus;
import org.tuvarna.chat.model.write.repository.ChatMessagesWrite;

import java.util.List;
import java.util.Random;

@Mock
@ApplicationScoped
public class ChatMessagesWriteMock implements ChatMessagesWrite {

    @Override
    public StatelessSession session() {
        return null;
    }

    @Override
    public MessagePersistenceStatus insertMessages(List<ChatMessageOperationalData> dataList) {
        boolean[] errors = new boolean[dataList.size()];
        return new MessagePersistenceStatus(false, errors);
    }

    @Override
    public ChatMessage save(ChatMessage entity) {
        entity.setId(new Random().nextLong(1000));
        return entity;
    }

    @Override
    public List<ChatMessage> saveAll(List<ChatMessage> entities) {
        long id = 1;
        for (ChatMessage e : entities) {
            e.setId(id++);
        }
        return entities;
    }

    @Override
    public int updateMessageById(long messageId, String content) {
        return 1;
    }

    @Override
    public int archiveMessageById(long messageId) {
        return 1;
    }
}