package org.tuvarnachat.model.repository.mocks;

import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.StatelessSession;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.repository.projection.ChatroomEventfulRead;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mock
@ApplicationScoped
public class ChatroomEventfulReadMock implements ChatroomEventfulRead {

    @Override
    public StatelessSession session() {
        return null;
    }

    @Override
    public ContentPage<ChatroomEventfulElement> findPageChatroomEventful(
            long userId,
            Instant lastActivity,
            Integer lastChatroomId,
            Long lastChatMessageId) {

        List<ChatroomEventfulElement> list = new ArrayList<>();

        for (int i = 1; i <= PAGE_SIZE; i++) {
            list.add(new ChatroomEventfulElement(
                    userId,
                    i,
                    "Chatroom " + i,
                    "Last message " + i,
                    i * 10L,
                    UUID.randomUUID().toString(),
                    Instant.now().minusSeconds(i * 60).toString()
            ));
        }

        return new ContentPage<>(list, false);
    }
}