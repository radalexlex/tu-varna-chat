package org.tuvarnachat.model.repository.mocks;

import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.StatelessSession;
import org.tuvarna.chat.model.entity.postgres.Chatroom;
import org.tuvarna.chat.model.read.repository.domain.ChatroomsRead;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Mock
@ApplicationScoped
public class ChatroomsReadMock implements ChatroomsRead {

    @Override
    public StatelessSession session() {
        return null; // not used in mock
    }

    @Override
    public Optional<Chatroom> findById(int id) {
        return Optional.of(buildChatroom(id));
    }

    @Override
    public List<Integer> findAllIdsByUserId(Long userId) {
        return List.of(1, 2, 3);
    }

    private Chatroom buildChatroom(int id) {
        Chatroom c = new Chatroom();
        c.setId(id);
        c.setName("Mock chatroom " + id);
        c.setCreatedAt(Instant.now().minusSeconds(id * 1000L));
        c.setDeleted(false);
        c.setLastRead((long) (id * 10L));
        return c;
    }
}