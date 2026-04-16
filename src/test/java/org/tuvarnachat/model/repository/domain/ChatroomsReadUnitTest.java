package org.tuvarnachat.model.repository.domain;

import org.hibernate.StatelessSession;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.model.entity.postgres.Chatroom;
import org.tuvarna.chat.model.read.repository.domain.ChatroomsRead;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChatroomsReadUnitTest {

    private ChatroomsRead createRepo(StatelessSession session) {
        return new ChatroomsRead() {
            @Override
            public StatelessSession session() {
                return session;
            }

            @Override
            public Optional<Chatroom> findById(int id) {
                return Optional.empty(); // fw methods are not tested
            }

        };
    }

    @Test
    void findAllIdsByUserId_returnsIds() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<Integer> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(Integer.class)))
                .thenReturn(query);

        when(query.setParameter(eq("userId"), eq(10L)))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of(1, 2, 3));

        ChatroomsRead repo = createRepo(session);

        List<Integer> result = repo.findAllIdsByUserId(10L);

        assertEquals(List.of(1, 2, 3), result);

        verify(session).createNativeQuery(contains("SELECT cm.id"), eq(Integer.class));
        verify(query).setParameter("userId", 10L);
        verify(query).getResultList();
    }

    @Test
    void findAllIdsByUserId_returnsEmptyList() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<Integer> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(Integer.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of());

        ChatroomsRead repo = createRepo(session);

        List<Integer> result = repo.findAllIdsByUserId(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllIdsByUserId_nullUserId_isStillPassedToQuery() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<Integer> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(Integer.class)))
                .thenReturn(query);

        when(query.setParameter(eq("userId"), isNull()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of(5));

        ChatroomsRead repo = createRepo(session);

        List<Integer> result = repo.findAllIdsByUserId(null);

        assertEquals(List.of(5), result);

        verify(query).setParameter("userId", null);
    }

    @Test
    void findAllIdsByUserId_executesCorrectSqlStructure() {
        StatelessSession session = mock(StatelessSession.class);
        NativeQuery<Integer> query = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(Integer.class)))
                .thenReturn(query);

        when(query.setParameter(anyString(), any()))
                .thenReturn(query);

        when(query.getResultList())
                .thenReturn(List.of());

        ChatroomsRead repo = createRepo(session);

        repo.findAllIdsByUserId(1L);

        verify(session).createNativeQuery(argThat(sql ->
                sql.contains("FROM chatroom cm") &&
                        sql.contains("INNER JOIN chatroom_user cu") &&
                        sql.contains("WHERE user_id = :userId")
        ), eq(Integer.class));
    }

    @Test
    void findAllIdsByUserId_multipleCalls_areIndependent() {
        StatelessSession session = mock(StatelessSession.class);

        NativeQuery<Integer> query1 = mock(NativeQuery.class);
        NativeQuery<Integer> query2 = mock(NativeQuery.class);

        when(session.createNativeQuery(anyString(), eq(Integer.class)))
                .thenReturn(query1)
                .thenReturn(query2);

        when(query1.setParameter(anyString(), any())).thenReturn(query1);
        when(query2.setParameter(anyString(), any())).thenReturn(query2);

        when(query1.getResultList()).thenReturn(List.of(1));
        when(query2.getResultList()).thenReturn(List.of(2, 3));

        ChatroomsRead repo = createRepo(session);

        List<Integer> r1 = repo.findAllIdsByUserId(1L);
        List<Integer> r2 = repo.findAllIdsByUserId(2L);

        assertEquals(List.of(1), r1);
        assertEquals(List.of(2, 3), r2);
    }
}