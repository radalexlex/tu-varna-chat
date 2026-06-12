//package org.tuvarnachat.api.service.services;
//
//import io.quarkus.test.InjectMock;
//import io.quarkus.test.junit.QuarkusTest;
//import jakarta.inject.Inject;
//import org.junit.jupiter.api.Test;
//import org.tuvarna.chat.application.api.service.ChatroomUserService;
//import org.tuvarna.chat.application.api.service.impl.ChatroomServiceImpl;
//import org.tuvarna.chat.application.exceptions.service.ChatroomServiceException;
//import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
//import org.tuvarna.chat.model.read.dto.ChatroomOverview;
//import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
//import org.tuvarna.chat.model.read.dto.ContentPage;
//import org.tuvarna.chat.model.read.query.DetailQuery;
//import org.tuvarna.chat.model.read.query.PageQuery;
//import org.tuvarna.chat.model.read.query.TotalQuery;
//import org.tuvarna.chat.model.read.query.handler.QueryHandler;
//import org.tuvarna.chat.model.read.query.page.data.ChatroomEventfulPageData;
//import org.tuvarna.chat.model.write.command.ChatroomCommand;
//import org.tuvarna.chat.model.write.command.handler.CommandHandler;
//
//import java.time.Instant;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@QuarkusTest
//class ChatroomServiceUnitTest {
//
//    @Inject
//    ChatroomServiceImpl service;
//
//    @InjectMock
//    CommandHandler<Integer, ChatroomCommand> commandHandler;
//
//    @InjectMock
//    QueryHandler<ChatroomOverview, DetailQuery<Integer>> roomQueryHandler;
//
//    @InjectMock
//    QueryHandler<List<Integer>, TotalQuery<Long>> totalQueryHandler;
//
//    @InjectMock
//    QueryHandler<ContentPage<ChatroomEventfulElement>,
//            PageQuery<Long, ChatroomEventfulPageData>> eventfulQueryHandler;
//
//    @InjectMock
//    ChatroomUserService chatroomUserService;
//
//
//    @Test
//    void createChatroom_success() {
//        when(commandHandler.handleCommand(any())).thenReturn(10);
//        when(chatroomUserService.addFirstChatroomUser(1L, 10)).thenReturn(1);
//
//        int result = service.createChatroom(1L, "room");
//
//        assertEquals(1, result);
//    }
//
//    @Test
//    void createChatroom_blankName() {
//        assertThrows(ChatroomServiceException.class,
//                () -> service.createChatroom(1L, " "));
//    }
//
//    @Test
//    void updateChatroomName_success() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "ADMIN", "ACTIVE", "", null
//        );
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        when(commandHandler.handleCommand(any())).thenReturn(1);
//
//        int result = service.updateChatroomName(1L, 1, "new");
//
//        assertEquals(1, result);
//    }
//
//    @Test
//    void updateChatroomName_invalidId() {
//        assertThrows(ChatroomServiceException.class,
//                () -> service.updateChatroomName(1L, 0, "new"));
//    }
//
//    @Test
//    void updateChatroomName_blankName() {
//        assertThrows(ChatroomServiceException.class,
//                () -> service.updateChatroomName(1L, 1, ""));
//    }
//
//    @Test
//    void updateChatroomName_notFound() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "ADMIN", "ACTIVE", "", null
//        );
//        String newName = "name";
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        when(commandHandler.handleCommand(new ChatroomCommand.UpdateChatroomName(
//                user.chatroomId(), newName)))
//                .thenReturn(0);
//
//        assertThrows(ChatroomServiceException.class,
//                () -> service.updateChatroomName(1L, 1, "new"));
//    }
//
//
//    @Test
//    void updateLastRead_success() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "USER", "ACTIVE", "", null
//        );
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        when(commandHandler.handleCommand(any())).thenReturn(1);
//
//        int result = service.updateLastReadStatus(1L, 1, 100L);
//
//        assertEquals(1, result);
//    }
//
//    @Test
//    void updateLastRead_invalidChatroom() {
//        assertThrows(ChatroomServiceException.class,
//                () -> service.updateLastReadStatus(1L, 0, 10));
//    }
//
//    @Test
//    void updateLastRead_negativeValue() {
//        assertThrows(ChatroomServiceException.class,
//                () -> service.updateLastReadStatus(1L, 1, -1));
//    }
//
//
//    @Test
//    void archiveChatroom_success() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "ADMIN", "ACTIVE", "", null
//        );
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        when(commandHandler.handleCommand(any())).thenReturn(1);
//
//        int result = service.archiveChatroom(1L, 1);
//
//        assertEquals(1, result);
//    }
//
//    @Test
//    void archiveChatroom_invalidId() {
//        assertThrows(ChatroomServiceException.class,
//                () -> service.archiveChatroom(1L, 0));
//    }
//
//    @Test
//    void archiveChatroom_notFound() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "ADMIN", "ACTIVE", "", null
//        );
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        when(commandHandler.handleCommand(any())).thenReturn(0);
//
//        assertThrows(ChatroomServiceException.class,
//                () -> service.archiveChatroom(1L, 1));
//    }
//
//
//    @Test
//    void getEventful_firstPage() {
//        ContentPage<ChatroomEventfulElement> page = mock(ContentPage.class);
//
//        when(eventfulQueryHandler.handleQuery(any())).thenReturn(page);
//
//        var result = service.getChatroomEventfulElements(1L, null, null, null);
//
//        assertEquals(page, result);
//    }
//
//    @Test
//    void getEventful_withCursor() {
//        ContentPage<ChatroomEventfulElement> page = mock(ContentPage.class);
//
//        when(eventfulQueryHandler.handleQuery(any())).thenReturn(page);
//
//        var result = service.getChatroomEventfulElements(
//                1L,
//                Instant.now(),
//                1,
//                1L
//        );
//
//        assertEquals(page, result);
//    }
//
//    @Test
//    void getEventful_invalidPagination() {
//        assertThrows(ChatroomServiceException.class,
//                () -> service.getChatroomEventfulElements(1L, Instant.now(), null, null));
//    }
//
//
//    @Test
//    void getChatroomOverview_success() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "USER", "ACTIVE", "", null
//        );
//
//        ChatroomOverview overview = mock(ChatroomOverview.class);
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        when(roomQueryHandler.handleQuery(any()))
//                .thenReturn(overview);
//
//        var result = service.getChatroomOverview(1L, 1);
//
//        assertEquals(overview, result);
//    }
//
//
//    @Test
//    void getChatroomIds_success() {
//        when(totalQueryHandler.handleQuery(any()))
//                .thenReturn(List.of(1, 2, 3));
//
//        var result = service.getChatroomIdsForUser(1L);
//
//        assertEquals(3, result.size());
//    }
//
//    @Test
//    void getChatroomIds_exception() {
//        when(totalQueryHandler.handleQuery(any()))
//                .thenThrow(new RuntimeException());
//
//        assertThrows(RuntimeException.class,
//                () -> service.getChatroomIdsForUser(1L));
//    }
//}