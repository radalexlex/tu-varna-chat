package org.tuvarnachat.model.read.query.handler;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.model.read.query.TotalQuery;
import org.tuvarna.chat.model.read.query.handler.impl.ChatroomTotalQueryHandler;
import org.tuvarnachat.model.repository.mocks.ChatroomsReadMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ChatroomTotalQueryHandlerUnitTest {

    @Inject
    ChatroomTotalQueryHandler handler;

    @Inject
    ChatroomsReadMock repository;

    @Test
    void handleQuery_getAllForCommon_returnsIds() {
        Long userId = 42L;

        var query = new TotalQuery.GetAllForCommon<>(userId);

        List<Integer> result = handler.handleQuery(query);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void handleQuery_multipleCalls_consistentResults() {
        Long userId = 99L;

        var query = new TotalQuery.GetAllForCommon<>(userId);

        List<Integer> first = handler.handleQuery(query);
        List<Integer> second = handler.handleQuery(query);

        assertEquals(first, second);
    }

    @Test
    void handleQuery_differentUserIds_sameMockedResult() {
        var query1 = new TotalQuery.GetAllForCommon<>(1L);
        var query2 = new TotalQuery.GetAllForCommon<>(2L);

        List<Integer> result1 = handler.handleQuery(query1);
        List<Integer> result2 = handler.handleQuery(query2);

        // Mock ignores userId, so results should match
        assertEquals(result1, result2);
        assertEquals(List.of(1, 2, 3), result1);
    }
}