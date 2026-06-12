//package org.tuvarnachat.model.read.query.handler;
//
//import io.quarkus.test.junit.QuarkusTest;
//import jakarta.inject.Inject;
//import org.junit.jupiter.api.Test;
//import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
//import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
//import org.tuvarna.chat.model.read.query.DetailQuery;
//import org.tuvarna.chat.model.read.query.handler.impl.ChatroomUserQueryDetailHandler;
//import org.tuvarna.chat.utils.Pair;
//import org.tuvarnachat.model.repository.mocks.ChatroomUsersReadMock;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@QuarkusTest
//class ChatroomUserQueryDetailHandlerUnitTest {
//
//    @Inject
//    ChatroomUserQueryDetailHandler handler;
//
//    @Inject
//    ChatroomUsersReadMock repository;
//
//
//    @Test
//    void handleQuery_validUser_returnsMappedDetails() {
//
//        long userId = 1L;
//        int chatroomId = 1;
//
//        var query = new DetailQuery.GetData<>(
//                new Pair<>(userId, chatroomId)
//        );
//
//        ChatroomUserDetails result = handler.handleQuery(query);
//
//        assertNotNull(result);
//
//        assertEquals(userId, result.userId());
//        assertEquals(chatroomId, result.chatroomId());
//
//        assertEquals("MEMBER", result.role());
//        assertEquals("ACTIVE", result.status());
//
//        assertNotNull(result.timeAdded());
//        assertEquals(0L, result.lastRead());
//    }
//
//
//    @Test
//    void handleQuery_mapsAllFieldsCorrectly() {
//
//        var query = new DetailQuery.GetData<>(
//                new Pair<>(2L, 1)
//        );
//
//        ChatroomUserDetails result = handler.handleQuery(query);
//
//        assertEquals(2L, result.userId());
//        assertEquals(1, result.chatroomId());
//        assertEquals(2L, result.id());
//
//        assertEquals("MEMBER", result.role());
//        assertEquals("ACTIVE", result.status());
//
//        assertNotNull(result.timeAdded());
//        assertFalse(result.timeAdded().isBlank());
//    }
//
//
//    @Test
//    void handleQuery_sameUserDifferentChatroom_returnsCorrectMapping() {
//
//        long userId = 1L;
//
//        var query = new DetailQuery.GetData<>(
//                new Pair<>(userId, 2) // different chatroom
//        );
//
//        ChatroomUserDetails result = handler.handleQuery(query);
//
//        assertEquals(userId, result.userId());
//        assertEquals(2, result.chatroomId());
//    }
//
//
//    @Test
//    void handleQuery_repositoryResultMatchesResponse() {
//
//        long userId = 1L;
//        int chatroomId = 1;
//
//        var query = new DetailQuery.GetData<>(
//                new Pair<>(userId, chatroomId)
//        );
//
//        ChatroomUser entity = repository
//                .getUserByUserIdAndChatroomId(userId, chatroomId)
//                .orElseThrow();
//
//        ChatroomUserDetails result = handler.handleQuery(query);
//
//        assertEquals(entity.getId(), result.id());
//        assertEquals(entity.getUserId(), result.userId());
//        assertEquals(entity.getChatroomId(), result.chatroomId());
//        assertEquals(entity.getRole().toString(), result.role());
//        assertEquals(entity.getStatus().toString(), result.status());
//        assertEquals(entity.getLastRead(), result.lastRead());
//    }
//}