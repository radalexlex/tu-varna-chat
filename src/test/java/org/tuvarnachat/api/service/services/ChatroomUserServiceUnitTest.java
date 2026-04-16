package org.tuvarnachat.api.service.services;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.application.api.controller.client.FriendServiceApiClient;
import org.tuvarna.chat.application.api.controller.dto.friend.PersonDto;
import org.tuvarna.chat.application.api.service.impl.ChatroomUserServiceImpl;
import org.tuvarna.chat.application.exceptions.notfound.ChatroomUserNotFoundException;
import org.tuvarna.chat.application.exceptions.service.ChatroomUserServiceException;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.DetailQuery;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatroomUserPageData;
import org.tuvarna.chat.model.write.command.ChatroomUserCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.dto.ChatroomUsersSaveData;
import org.tuvarna.chat.utils.Pair;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@QuarkusTest
class ChatroomUserServiceUnitTest {

    @Inject
    ChatroomUserServiceImpl service;

    @InjectMock
    QueryHandler<ChatroomUserDetails, DetailQuery<Pair<Long, Integer>>> userQuery;

    @InjectMock
    QueryHandler<ContentPage<ChatroomUserDetails>,
            PageQuery<Integer, ChatroomUserPageData>> userPageQuery;

    @InjectMock
    CommandHandler<Integer, ChatroomUserCommand> commandHandler;

    @InjectMock
    @RestClient
    FriendServiceApiClient friendServiceApiClient;

    private ChatroomUserDetails activeUser(long userId, int chatroomId) {
        return new ChatroomUserDetails(
                1L, chatroomId, userId, "ADMIN", "ACTIVE", "", null
        );
    }


    @Test
    void getUsers_firstPage() {
        when(userQuery.handleQuery(any()))
                .thenReturn(activeUser(1L, 1));

        ContentPage<ChatroomUserDetails> page = mock(ContentPage.class);
        when(userPageQuery.handleQuery(any())).thenReturn(page);

        var result = service.getUsers(1L, 1, null, null);

        assertEquals(page, result);
    }

    @Test
    void getUsers_withCursor() {
        when(userQuery.handleQuery(any()))
                .thenReturn(activeUser(1L, 1));

        ContentPage<ChatroomUserDetails> page = mock(ContentPage.class);
        when(userPageQuery.handleQuery(any())).thenReturn(page);

        var result = service.getUsers(
                1L, 1,
                Instant.now(),
                10
        );

        assertEquals(page, result);
    }

    @Test
    void getUsers_invalidPagination() {
        when(userQuery.handleQuery(any()))
                .thenReturn(activeUser(1L, 1));

        assertThrows(ChatroomUserServiceException.class,
                () -> service.getUsers(1L, 1, Instant.now(), null));
    }


    @Test
    void addUsers_success() {
        when(userQuery.handleQuery(any()))
                .thenReturn(activeUser(1L, 1));

        when(friendServiceApiClient.getAllFriendsOfPerson(1L))
                .thenReturn(List.of(
                        new PersonDto(2L, "name1", 1234L),
                        new PersonDto(3L, "name2", 4321L)));

        when(commandHandler.handleCommand(any())).thenReturn(2);

        var saveData = new ChatroomUsersSaveData(
                1,
                Map.of(2L, ChatroomRole.MEMBER, 3L, ChatroomRole.MEMBER)
        );

        int result = service.addUsers(1L, saveData);

        assertEquals(2, result);
    }

    @Test
    void addUsers_userNotFriend() {
        when(userQuery.handleQuery(any()))
                .thenReturn(activeUser(1L, 1));

        when(friendServiceApiClient.getAllFriendsOfPerson(1L))
                .thenReturn(List.of(new PersonDto(2L, "name1", 1234L)));
        var saveData = new ChatroomUsersSaveData(
                1,
                Map.of(3L, ChatroomRole.MEMBER)
        );

        assertThrows(ChatroomUserServiceException.class,
                () -> service.addUsers(1L, saveData));
    }


    @Test
    void changeUserRole_success() {
        when(userQuery.handleQuery(any()))
                .thenReturn(activeUser(1L, 1));

        when(commandHandler.handleCommand(any())).thenReturn(1);

        int result = service.changeUserRole(1L, 1, 2L, "member");

        assertEquals(1, result);
    }

    @Test
    void changeUserRole_notFound() {
        when(userQuery.handleQuery(any()))
                .thenReturn(activeUser(1L, 1));

        when(commandHandler.handleCommand(any())).thenReturn(0);

        assertThrows(ChatroomUserServiceException.class,
                () -> service.changeUserRole(1L, 1, 2L, "member"));
    }

    @Test
    void changeUserRole_invalidRole() {
        when(userQuery.handleQuery(any()))
                .thenReturn(activeUser(1L, 1));

        assertThrows(RuntimeException.class,
                () -> service.changeUserRole(1L, 1, 2L, "invalid"));
    }


    @Test
    void changeMembership_self() {
        ChatroomUserDetails self = activeUser(1L, 1);

        when(userQuery.handleQuery(any()))
                .thenReturn(self);

        when(commandHandler.handleCommand(any())).thenReturn(1);

        int result = service.changeUserMembershipStatus(
                1L, 1, 1L, "left"
        );

        assertEquals(1, result);
    }

    @Test
    void changeMembership_otherUser() {
        ChatroomUserDetails requester = activeUser(1L, 1);
        ChatroomUserDetails target = activeUser(2L, 1);

        when(userQuery.handleQuery(any()))
                .thenReturn(requester) // first call
                .thenReturn(target);   // second call

        when(commandHandler.handleCommand(any())).thenReturn(1);

        int result = service.changeUserMembershipStatus(
                1L, 1, 2L, "left"
        );

        assertEquals(1, result);
    }

    @Test
    void changeMembership_notFound() {
        ChatroomUserDetails self = activeUser(1L, 1);

        when(userQuery.handleQuery(any()))
                .thenReturn(self);

        when(commandHandler.handleCommand(any())).thenReturn(0);

        assertThrows(ChatroomUserServiceException.class,
                () -> service.changeUserMembershipStatus(1L, 1, 1L, "left"));
    }


    @Test
    void addFirstChatroomUser_success() {
        when(commandHandler.handleCommand(any())).thenReturn(1);

        int result = service.addFirstChatroomUser(1L, 1);

        assertEquals(1, result);
    }


    @Test
    void getUserDetailsForSelf_success() {
        ChatroomUserDetails user = activeUser(1L, 1);

        when(userQuery.handleQuery(any())).thenReturn(user);

        var result = service.getUserDetailsForSelf(1L, 1);

        assertEquals(user, result);
    }

    @Test
    void getUserDetailsForSelf_exception() {
        when(userQuery.handleQuery(any()))
                .thenThrow(new ChatroomUserNotFoundException("fail"));

        assertThrows(ChatroomUserNotFoundException.class,
                () -> service.getUserDetailsForSelf(1L, 1));
    }


    @Test
    void getUserDetailsForRequester_success() {
        ChatroomUserDetails requester = activeUser(1L, 1);
        ChatroomUserDetails target = activeUser(2L, 1);

        when(userQuery.handleQuery(any()))
                .thenReturn(requester)
                .thenReturn(target);

        var result = service.getUserDetailsForRequester(1L, 2L, 1);

        assertEquals(target, result);
    }


    @Test
    void updateLastReadStatus_success() {
        ChatroomUserDetails user = activeUser(1L, 1);

        when(userQuery.handleQuery(any())).thenReturn(user);
        when(commandHandler.handleCommand(any())).thenReturn(1);

        int result = service.updateLastReadStatus(1L, 1, 100L);

        assertEquals(1, result);
    }
}