package org.tuvarnachat.api.service.services;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.application.api.service.ChatroomUserService;
import org.tuvarna.chat.application.api.service.impl.ChatMessageServiceImpl;
import org.tuvarna.chat.application.exceptions.persistence.DataPersistenceException;
import org.tuvarna.chat.application.exceptions.service.ChatMessageServiceException;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatMessagePageData;
import org.tuvarna.chat.model.write.command.ChatMessageMutationCommand;
import org.tuvarna.chat.model.write.command.ChatMessagePersistenceCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;
import org.tuvarna.chat.model.write.dto.MessagePersistenceStatus;
import org.tuvarna.chat.model.write.dto.enums.AckStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ChatMessageServiceUnitTest {

    @Inject
    ChatMessageServiceImpl service;

    @InjectMock
    CommandHandler<MessagePersistenceStatus, ChatMessagePersistenceCommand> persistenceHandler;

    @InjectMock
    CommandHandler<Integer, ChatMessageMutationCommand> mutationHandler;

    @InjectMock
    QueryHandler<ContentPage<ChatMessageElement>, PageQuery<Integer, ChatMessagePageData>> queryHandler;

    @InjectMock
    ChatroomUserService chatroomUserService;


    @Test
    void addMessages_success_allSaved() {
        List<ChatMessageOperationalData> input = List.of(
                mock(ChatMessageOperationalData.class),
                mock(ChatMessageOperationalData.class)
        );

        when(persistenceHandler.handleCommand(any()))
                .thenReturn(new MessagePersistenceStatus(false, new boolean[]{false, false}));

        Map<AckStatus, List<ChatMessageOperationalData>> result =
                service.addMessages(input);

        assertEquals(2, result.get(AckStatus.SUCCESS).size());
        assertTrue(result.get(AckStatus.FAILURE).isEmpty());
    }

    @Test
    void addMessages_partialFailure() {
        List<ChatMessageOperationalData> input = List.of(
                mock(ChatMessageOperationalData.class),
                mock(ChatMessageOperationalData.class)
        );

        when(persistenceHandler.handleCommand(any()))
                .thenReturn(new MessagePersistenceStatus(true, new boolean[]{true, false}));

        Map<AckStatus, List<ChatMessageOperationalData>> result =
                service.addMessages(input);

        assertEquals(1, result.get(AckStatus.SUCCESS).size());
        assertEquals(1, result.get(AckStatus.FAILURE).size());
    }

    @Test
    void addMessages_emptyInput_shouldThrow() {
        assertThrows(ChatMessageServiceException.class,
                () -> service.addMessages(Collections.emptyList()));
    }

    @Test
    void addMessages_mismatchedIndexes_shouldThrow() {
        List<ChatMessageOperationalData> input = List.of(
                mock(ChatMessageOperationalData.class)
        );

        when(persistenceHandler.handleCommand(any()))
                .thenReturn(new MessagePersistenceStatus(false, new boolean[]{false, false}));

        assertThrows(ChatMessageServiceException.class,
                () -> service.addMessages(input));
    }


    @Test
    void archiveMessage_success_asOwner() {
        ChatMessageElement msg = mock(ChatMessageElement.class);
        when(msg.senderId()).thenReturn(1L);
        when(msg.id()).thenReturn(10L);

        ChatroomUserDetails user = new ChatroomUserDetails(
                1L, 1, 1L, "USER", "ACTIVE", "", null
        );

        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
                .thenReturn(user);

        when(mutationHandler.handleCommand(any()))
                .thenReturn(1);

        int result = service.archiveMessage(1L, msg, 1);

        assertEquals(1, result);
    }

    @Test
    void archiveMessage_invalidChatroom() {
        ChatMessageElement msg = mock(ChatMessageElement.class);

        assertThrows(ChatMessageServiceException.class,
                () -> service.archiveMessage(1L, msg, 0));
    }

    @Test
    void archiveMessage_notAllowed() {
        ChatMessageElement msg = mock(ChatMessageElement.class);
        when(msg.senderId()).thenReturn(2L);
        when(msg.id()).thenReturn(10L);

        ChatroomUserDetails user = new ChatroomUserDetails(
                1L, 1, 1L, "USER", "ACTIVE", "", null
        );

        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
                .thenReturn(user);

        assertThrows(ChatMessageServiceException.class,
                () -> service.archiveMessage(1L, msg, 1));
    }

    @Test
    void archiveMessage_notFound() {
        ChatMessageElement msg = mock(ChatMessageElement.class);
        when(msg.senderId()).thenReturn(1L);
        when(msg.id()).thenReturn(10L);

        ChatroomUserDetails user = new ChatroomUserDetails(
                1L, 1, 1L, "USER", "ACTIVE", "", null
        );

        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
                .thenReturn(user);

        when(mutationHandler.handleCommand(any()))
                .thenReturn(0);

        assertThrows(ChatMessageServiceException.class,
                () -> service.archiveMessage(1L, msg, 1));
    }


    @Test
    void updateMessage_success() {
        ChatMessageElement msg = mock(ChatMessageElement.class);
        when(msg.senderId()).thenReturn(1L);
        when(msg.id()).thenReturn(10L);

        ChatroomUserDetails user = new ChatroomUserDetails(
                1L, 1, 1L, "USER", "ACTIVE", "", null
        );

        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
                .thenReturn(user);

        when(mutationHandler.handleCommand(any()))
                .thenReturn(1);

        int result = service.updateMessage(1L, msg, 1, "updated");

        assertEquals(1, result);
    }

    @Test
    void updateMessage_blankContent() {
        ChatMessageElement msg = mock(ChatMessageElement.class);

        assertThrows(ChatMessageServiceException.class,
                () -> service.updateMessage(1L, msg, 1, " "));
    }

    @Test
    void updateMessage_tooLong() {
        ChatMessageElement msg = mock(ChatMessageElement.class);

        String longText = "a".repeat(5001);

        assertThrows(ChatMessageServiceException.class,
                () -> service.updateMessage(1L, msg, 1, longText));
    }

    @Test
    void updateMessage_notOwner() {
        ChatMessageElement msg = mock(ChatMessageElement.class);
        when(msg.senderId()).thenReturn(2L);

        ChatroomUserDetails user = new ChatroomUserDetails(
                1L, 1, 1L, "USER", "ACTIVE", "", null
        );

        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
                .thenReturn(user);

        assertThrows(ChatMessageServiceException.class,
                () -> service.updateMessage(1L, msg, 1, "text"));
    }

    @Test
    void updateMessage_notFound() {
        ChatMessageElement msg = mock(ChatMessageElement.class);
        when(msg.senderId()).thenReturn(1L);
        when(msg.id()).thenReturn(10L);

        ChatroomUserDetails user = new ChatroomUserDetails(
                1L, 1, 1L, "USER", "ACTIVE", "", null
        );

        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
                .thenReturn(user);

        when(mutationHandler.handleCommand(any()))
                .thenReturn(0);

        assertThrows(ChatMessageServiceException.class,
                () -> service.updateMessage(1L, msg, 1, "text"));
    }


//    @Test
//    void getMessagePage_firstPage() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "USER", "ACTIVE", "", null
//        );
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        ContentPage<ChatMessageElement> page = mock(ContentPage.class);
//
//        when(queryHandler.handleQuery(any())).thenReturn(page);
//
//        ContentPage<ChatMessageElement> result =
//                service.getMessagePage(1L, 1, null, null, true);
//
//        assertEquals(page, result);
//    }

//    @Test
//    void getMessagePage_withCursor() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "USER", "ACTIVE", "", null
//        );
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        ContentPage<ChatMessageElement> page = mock(ContentPage.class);
//
//        when(queryHandler.handleQuery(any())).thenReturn(page);
//
//        ContentPage<ChatMessageElement> result =
//                service.getMessagePage(
//                        1L,
//                        1,
//                        Instant.now(),
//                        10,
//                        true
//                );
//
//        assertEquals(page, result);
//    }

//    @Test
//    void getMessagePage_invalidPagination() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "USER", "ACTIVE", "", null
//        );
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        assertThrows(ChatMessageServiceException.class,
//                () -> service.getMessagePage(1L, 1, Instant.now(), null, true));
//    }
//

    @Test
    void addMessages_allFailed() {
        List<ChatMessageOperationalData> input = List.of(
                mock(ChatMessageOperationalData.class),
                mock(ChatMessageOperationalData.class)
        );

        when(persistenceHandler.handleCommand(any()))
                .thenReturn(new MessagePersistenceStatus(true, new boolean[]{true, true}));

        var result = service.addMessages(input);

        assertTrue(result.get(AckStatus.SUCCESS).isEmpty());
        assertEquals(2, result.get(AckStatus.FAILURE).size());
    }

    @Test
    void archiveMessage_superUser_canArchiveOthers() {
        ChatMessageElement msg = mock(ChatMessageElement.class);
        when(msg.senderId()).thenReturn(2L);
        when(msg.id()).thenReturn(10L);

        ChatroomUserDetails user = new ChatroomUserDetails(
                1L, 1, 1L, "ADMIN", "ACTIVE", "", null
        );

        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
                .thenReturn(user);

        when(mutationHandler.handleCommand(any())).thenReturn(1);

        int result = service.archiveMessage(1L, msg, 1);

        assertEquals(1, result);
    }

    @Test
    void archiveMessage_userNotInChatroom() {
        when(chatroomUserService.getUserDetailsForSelf(anyLong(), anyInt()))
                .thenReturn(null);

        ChatMessageElement msg = mock(ChatMessageElement.class);

        assertThrows(ChatMessageServiceException.class,
                () -> service.archiveMessage(1L, msg, 1));
    }

    @Test
    void updateMessage_shouldWrapApplicationException() {
        ChatMessageElement msg = mock(ChatMessageElement.class);
        when(msg.senderId()).thenReturn(1L);

        ChatroomUserDetails user = new ChatroomUserDetails(
                1L, 1, 1L, "USER", "ACTIVE", "", null
        );

        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
                .thenReturn(user);

        when(mutationHandler.handleCommand(any()))
                .thenThrow(new DataPersistenceException("fail"));

        assertThrows(ChatMessageServiceException.class,
                () -> service.updateMessage(1L, msg, 1, "text"));
    }

//    @Test
//    void getMessagePage_queryThrows() {
//        ChatroomUserDetails user = new ChatroomUserDetails(
//                1L, 1, 1L, "USER", "ACTIVE", "", null
//        );
//
//        when(chatroomUserService.getUserDetailsForSelf(1L, 1))
//                .thenReturn(user);
//
//        when(queryHandler.handleQuery(any()))
//                .thenThrow(new PaginationException("fail"));
//
//        assertThrows(ChatMessageServiceException.class,
//                () -> service.getMessagePage(1L, 1, null, null, true));
//    }


}