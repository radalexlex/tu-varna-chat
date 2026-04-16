package org.tuvarnachat.model.repository.domain;

import jakarta.persistence.PersistenceException;
import org.hibernate.StatelessSession;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;
import org.tuvarna.chat.model.write.dto.MessagePersistenceStatus;
import org.tuvarna.chat.model.write.repository.ChatMessagesWrite;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatMessagesWriteUnitTest {

    private ChatMessagesWrite repo(StatelessSession session) {
        return new ChatMessagesWrite() { // are not needed, covered by framework
            @Override
            public StatelessSession session() {
                return session;
            }

            @Override
            public ChatMessage save(ChatMessage entity) {
                return null;
            }

            @Override
            public List<ChatMessage> saveAll(List<ChatMessage> entities) {
                return List.of();
            }

            @Override
            public int updateMessageById(long messageId, String content) {
                return 0;
            }

            @Override
            public int archiveMessageById(long messageId) {
                return 0;
            }
        };
    }

    @Test
    void returnsEmptyStatus_whenInputIsNull() {
        ChatMessagesWrite repo = repo(mock(StatelessSession.class));

        MessagePersistenceStatus result = repo.insertMessages(null);

        assertFalse(result.errored());
        assertEquals(0, result.errorIndexes().length);
    }

    @Test
    void returnsEmptyStatus_whenInputIsEmpty() {
        ChatMessagesWrite repo = repo(mock(StatelessSession.class));

        MessagePersistenceStatus result =
                repo.insertMessages(List.of());

        assertFalse(result.errored());
        assertEquals(0, result.errorIndexes().length);
    }

    @Test
    void insertsAllMessages_successfully() {
        StatelessSession session = mock(StatelessSession.class);
        ChatMessagesWrite repo = repo(session);

        List<ChatMessageOperationalData> input = List.of(
                new ChatMessageOperationalData(
                        "550e8400-e29b-41d4-a716-446655440000",
                        1,
                        10L,
                        10L,
                        "hello",
                        "2026-01-01T00:00:00Z"
                ),
                new ChatMessageOperationalData(
                        "550e8400-e29b-41d4-a716-446655440001",
                        1,
                        11L,
                        11L,
                        "world",
                        "2026-01-01T00:00:00Z"
                )
        );

        MessagePersistenceStatus result = repo.insertMessages(input);

        assertFalse(result.errored());
        assertEquals(2, result.errorIndexes().length);
        assertFalse(result.errorIndexes()[0]);
        assertFalse(result.errorIndexes()[1]);

        verify(session, times(2)).insert(any(ChatMessage.class));
    }

    @Test
    void marksErrorIndex_onPersistenceException() {
        StatelessSession session = mock(StatelessSession.class);
        ChatMessagesWrite repo = repo(session);

        doThrow(new PersistenceException("DB error"))
                .when(session).insert(any(ChatMessage.class));

        List<ChatMessageOperationalData> input =
                List.of(validData("550e8400-e29b-41d4-a716-446655440000"));

        MessagePersistenceStatus result = repo.insertMessages(input);

        assertTrue(result.errored());
        assertTrue(result.errorIndexes()[0]);

        verify(session).insert(any(ChatMessage.class));
    }

    @Test
    void marksErrorIndex_onGenericException() {
        StatelessSession session = mock(StatelessSession.class);
        ChatMessagesWrite repo = repo(session);

        doThrow(new RuntimeException("boom"))
                .when(session).insert(any(ChatMessage.class));

        List<ChatMessageOperationalData> input =
                List.of(validData("550e8400-e29b-41d4-a716-446655440000"));

        MessagePersistenceStatus result = repo.insertMessages(input);

        assertTrue(result.errored());
        assertTrue(result.errorIndexes()[0]);
    }

    @Test
    void mixedSuccessAndFailure() {
        StatelessSession session = mock(StatelessSession.class);
        ChatMessagesWrite repo = repo(session);

        AtomicInteger counter = new AtomicInteger(0);

        doAnswer(invocation -> {
            int i = counter.getAndIncrement();

            if (i == 1) {
                throw new PersistenceException("DB error");
            }

            return null;
        }).when(session).insert(any(ChatMessage.class));

        List<ChatMessageOperationalData> input = List.of(
                validData("550e8400-e29b-41d4-a716-446655440000"),
                validData("550e8400-e29b-41d4-a716-446655440001"),
                validData("550e8400-e29b-41d4-a716-446655440002")
        );

        MessagePersistenceStatus result = repo.insertMessages(input);

        assertTrue(result.errored());

        assertFalse(result.errorIndexes()[0]);
        assertTrue(result.errorIndexes()[1]);
        assertFalse(result.errorIndexes()[2]);
    }

    private ChatMessageOperationalData validData(String uuid) {
        return new ChatMessageOperationalData(
                uuid,
                1,
                10L,
                10L,
                "msg",
                "2026-01-01T00:00:00Z"
        );
    }
}