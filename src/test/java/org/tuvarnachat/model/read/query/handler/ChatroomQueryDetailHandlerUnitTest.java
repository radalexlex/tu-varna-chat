package org.tuvarnachat.model.read.query.handler;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;
import org.tuvarna.chat.model.read.query.DetailQuery;
import org.tuvarna.chat.model.read.query.handler.impl.ChatroomQueryDetailHandler;
import org.tuvarnachat.model.repository.mocks.ChatroomsReadMock;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ChatroomQueryDetailHandlerUnitTest {

    @Inject
    ChatroomQueryDetailHandler handler;

    @Inject
    ChatroomsReadMock repository;

    @Test
    void handleQuery_validId_returnsChatroomOverview() {
        int chatroomId = 1;

        var query = new DetailQuery.GetData<>(chatroomId);

        ChatroomOverview result = handler.handleQuery(query);

        assertNotNull(result);
        assertEquals(chatroomId, result.id());
        assertEquals("Mock chatroom " + chatroomId, result.name());
        assertNotNull(result.createdAt());
        assertEquals(chatroomId * 10L, result.lastRead());
    }

    @Test
    void handleQuery_mapsFieldsCorrectly() {
        int chatroomId = 2;

        var result = handler.handleQuery(new DetailQuery.GetData<>(chatroomId));

        assertNotNull(result.createdAt());
        assertTrue(result.createdAt().length() > 0);
        assertTrue(result.name().startsWith("Mock chatroom"));
        assertEquals(20L, result.lastRead());
    }

    @Test
    void handleQuery_multipleCalls_consistentResults() {
        int chatroomId = 3;

        var query = new DetailQuery.GetData<>(chatroomId);

        ChatroomOverview first = handler.handleQuery(query);
        ChatroomOverview second = handler.handleQuery(query);

        assertEquals(first.id(), second.id());
        assertEquals(first.name(), second.name());
        assertEquals(first.lastRead(), second.lastRead());
    }

    @Test
    void handleQuery_largeId_stillReturnsData() {
        int chatroomId = 999;

        var result = handler.handleQuery(new DetailQuery.GetData<>(chatroomId));

        assertNotNull(result);
        assertEquals(chatroomId, result.id());
        assertEquals("Mock chatroom " + chatroomId, result.name());
    }

    @Test
    void handleQuery_zeroId_edgeCase() {
        int chatroomId = 0;

        var result = handler.handleQuery(new DetailQuery.GetData<>(chatroomId));

        assertNotNull(result);
        assertEquals(chatroomId, result.id());
        assertEquals("Mock chatroom 0", result.name());
        assertEquals(0L, result.lastRead());
    }

    @Test
    void handleQuery_negativeId_edgeCase() {
        int chatroomId = -1;

        var result = handler.handleQuery(new DetailQuery.GetData<>(chatroomId));

        assertNotNull(result);
        assertEquals(chatroomId, result.id());
        assertEquals("Mock chatroom -1", result.name());
        assertEquals(-10L, result.lastRead());
    }

}