package org.tuvarnachat.model.repository.projection;

import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.repository.projection.ChatroomEventfulRead;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatroomEventfulReadUnitTest {

    private ChatroomEventfulRead repo(StatelessSession session) {
        return new ChatroomEventfulRead() {
            @Override
            public StatelessSession session() {
                return session;
            }
        };
    }

    private NativeQuery<ChatroomEventfulElement> mockQuery(StatelessSession session) {
        NativeQuery<ChatroomEventfulElement> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(ChatroomEventfulElement.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any())).thenReturn(query);

        return query;
    }

    @Test
    void returnsPageWithoutFollowing_whenBelowPageSize() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<ChatroomEventfulElement> query = mockQuery(session);

        List<ChatroomEventfulElement> data =
                List.of(mock(ChatroomEventfulElement.class));

        when(query.getResultList()).thenReturn(data);

        ChatroomEventfulRead repo = repo(session);

        ContentPage<ChatroomEventfulElement> result =
                repo.findPageChatroomEventful(1L, null, 1, 1L);

        assertEquals(data, result.content());
        assertFalse(result.hasFollowing());
    }

    @Test
    void returnsHasFollowingTrue_whenMoreThanPageSize() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<ChatroomEventfulElement> query = mockQuery(session);
        List<ChatroomEventfulElement> data = new ArrayList<>();
        // limit is 50
        for (int i = 0; i < 51; i++) {
            data.add(mock(ChatroomEventfulElement.class));
        }

        when(query.getResultList()).thenReturn(data);

        ChatroomEventfulRead repo = repo(session);

        ContentPage<ChatroomEventfulElement> result =
                repo.findPageChatroomEventful(1L, null, 1, 1L);

        assertTrue(result.hasFollowing());
    }

    @Test
    void removesExtraElement_whenPageSizeExceeded() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<ChatroomEventfulElement> query = mockQuery(session);

        List<ChatroomEventfulElement> data =
                new java.util.ArrayList<>();

        for (int i = 0; i < ChatroomEventfulRead.PAGE_SIZE + 1; i++) {
            data.add(mock(ChatroomEventfulElement.class));
        }

        when(query.getResultList()).thenReturn(data);

        ChatroomEventfulRead repo = repo(session);

        ContentPage<ChatroomEventfulElement> result =
                repo.findPageChatroomEventful(1L, null, 1, 1L);

        assertEquals(ChatroomEventfulRead.PAGE_SIZE, result.content().size());
        assertTrue(result.hasFollowing());
    }

    @Test
    void setsAllParametersCorrectly() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<ChatroomEventfulElement> query = mockQuery(session);

        when(query.getResultList()).thenReturn(List.of());

        ChatroomEventfulRead repo = repo(session);

        Instant lastActivity = Instant.parse("2024-01-01T00:00:00Z");

        repo.findPageChatroomEventful(
                10L,
                lastActivity,
                5,
                99L
        );

        verify(query).setParameter("userId", 10L);
        verify(query).setParameter("lastActivity", lastActivity);
        verify(query).setParameter("lastChatroomId", 5);
        verify(query).setParameter("lastChatMessageId", 99L);
        verify(query).setParameter("limit", ChatroomEventfulRead.PAGE_SIZE);
    }

    @Test
    void supportsNullCursorValues() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<ChatroomEventfulElement> query = mockQuery(session);

        when(query.getResultList()).thenReturn(List.of());

        ChatroomEventfulRead repo = repo(session);

        repo.findPageChatroomEventful(
                1L,
                null,
                null,
                null
        );

        verify(query).setParameter("lastActivity", null);
        verify(query).setParameter("lastChatroomId", null);
        verify(query).setParameter("lastChatMessageId", null);
    }

    @Test
    void usesExpectedSqlStructure() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<ChatroomEventfulElement> query = mockQuery(session);

        when(query.getResultList()).thenReturn(List.of());

        ChatroomEventfulRead repo = repo(session);

        repo.findPageChatroomEventful(1L, null, null, null);

        verify(session).createNativeQuery(argThat(sql ->
                sql.contains("FROM chatroom_user user_cu") &&
                        sql.contains("LEFT JOIN LATERAL") &&
                        sql.contains("ORDER BY") &&
                        sql.contains("LIMIT :limit + 1")
        ), eq(ChatroomEventfulElement.class));
    }

    @Test
    void returnsEmptyPage_whenNoData() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<ChatroomEventfulElement> query = mockQuery(session);

        when(query.getResultList()).thenReturn(List.of());

        ChatroomEventfulRead repo = repo(session);

        ContentPage<ChatroomEventfulElement> result =
                repo.findPageChatroomEventful(1L, null, null, null);

        assertTrue(result.content().isEmpty());
        assertFalse(result.hasFollowing());
    }
}