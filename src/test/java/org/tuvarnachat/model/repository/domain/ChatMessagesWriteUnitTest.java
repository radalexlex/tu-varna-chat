package org.tuvarnachat.model.repository.domain;

import jakarta.persistence.PersistenceException;
import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
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
        return new ChatMessagesWrite() {
            @Override
            public StatelessSession session() {
                return session;
            }

            @Override
            public ChatMessage save(ChatMessage entity) { return null; }

            @Override
            public List<ChatMessage> saveAll(List<ChatMessage> entities) { return List.of(); }

            @Override
            public int updateMessageById(long messageId, String content) { return 0; }

            @Override
            public int archiveMessageById(long messageId) { return 0; }
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

        MessagePersistenceStatus result = repo.insertMessages(List.of());

        assertFalse(result.errored());
        assertEquals(0, result.errorIndexes().length);
    }

    @Test
    void insertsAllMessages_successfully() {
        StatelessSession session = mock(StatelessSession.class);
        ChatMessagesWrite repo = repo(session);

        NativeQuery<ChatMessage> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(ChatMessage.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        when(query.executeUpdate())
                .thenReturn(1); // success for all

        List<ChatMessageOperationalData> input = List.of(
                validData("550e8400-e29b-41d4-a716-446655440000"),
                validData("550e8400-e29b-41d4-a716-446655440001")
        );

        MessagePersistenceStatus result = repo.insertMessages(input);

        assertFalse(result.errored());
        assertEquals(2, result.errorIndexes().length);
        assertFalse(result.errorIndexes()[0]);
        assertFalse(result.errorIndexes()[1]);

        verify(query, times(2)).executeUpdate();
    }

    @Test
    void marksErrorIndex_onPersistenceException() {
        StatelessSession session = mock(StatelessSession.class);
        ChatMessagesWrite repo = repo(session);

        NativeQuery<ChatMessage> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(ChatMessage.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        when(query.executeUpdate())
                .thenThrow(new PersistenceException("DB error"));

        List<ChatMessageOperationalData> input =
                List.of(validData("550e8400-e29b-41d4-a716-446655440000"));

        MessagePersistenceStatus result = repo.insertMessages(input);

        assertTrue(result.errored());
        assertTrue(result.errorIndexes()[0]);
    }

    @Test
    void marksErrorIndex_onGenericException() {
        StatelessSession session = mock(StatelessSession.class);
        ChatMessagesWrite repo = repo(session);

        NativeQuery<ChatMessage> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(ChatMessage.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        when(query.executeUpdate())
                .thenThrow(new RuntimeException("boom"));

        List<ChatMessageOperationalData> input =
                List.of(validData("550e8400-e29b-41d4-a716-446655440000"));

        MessagePersistenceStatus result = repo.insertMessages(input);

        assertTrue(result.errored());
        assertTrue(result.errorIndexes()[0]);
    }

    @Test
    void marksErrorIndex_whenSenderValidationFails() {
        StatelessSession session = mock(StatelessSession.class);
        ChatMessagesWrite repo = repo(session);

        NativeQuery<ChatMessage> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(ChatMessage.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        when(query.executeUpdate())
                .thenReturn(0); // sender validation failed

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

        NativeQuery<ChatMessage> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(ChatMessage.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        AtomicInteger counter = new AtomicInteger(0);

        when(query.executeUpdate()).thenAnswer(inv -> {
            int i = counter.getAndIncrement();
            if (i == 1) return 0; // fail second message
            return 1;
        });

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
