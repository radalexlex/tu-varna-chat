package org.tuvarna.chat.application.api.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.api.controller.client.FriendServiceApiClient;
import org.tuvarna.chat.application.api.controller.dto.friend.PersonDto;
import org.tuvarna.chat.application.api.service.ChatroomUserService;
import org.tuvarna.chat.application.api.service.validation.UserValidationHelper;
import org.tuvarna.chat.application.api.service.validation.ValidationHandler;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class ChatroomUserServiceImpl implements ChatroomUserService {

    private static final Logger log = LoggerFactory.getLogger(ChatroomUserServiceImpl.class);

    QueryHandler <ChatroomUserDetails, DetailQuery<Pair<Long,Integer>>> userQuery;
    QueryHandler <ContentPage<ChatroomUserDetails>,
            PageQuery<Integer, ChatroomUserPageData>> userPagedQueryHandler;

    CommandHandler<Integer, ChatroomUserCommand> commandHandler;

    @RestClient
    FriendServiceApiClient friendServiceApiClient;

    @Inject
    public ChatroomUserServiceImpl(@Named("ChatroomUserPageQueryHandler")
                               QueryHandler<ContentPage<ChatroomUserDetails>, PageQuery<Integer, ChatroomUserPageData>> userPagedQueryHandler,
                                   @Named("ChatroomUserCommandHandler")
                               CommandHandler<Integer, ChatroomUserCommand> commandHandler,
                                   @Named("ChatroomUserQueryDetailHandler")
                               QueryHandler<ChatroomUserDetails, DetailQuery<Pair<Long,Integer>>> userQuery) {
        this.userPagedQueryHandler = userPagedQueryHandler;
        this.commandHandler = commandHandler;
        this.userQuery = userQuery;

    }

    @Override
    public ContentPage<ChatroomUserDetails> getUsers(long requestingUserId,
                                                     int chatroomId,
                                                     Instant oldestAdditionTimestamp,
                                                     Integer oldestAdditionId) {

        ChatroomUserDetails cu = getUserDetailsForSelf(requestingUserId, chatroomId);

        boolean hasExtendedPermissions;

        try {
            ValidationHandler<ChatroomUserDetails> validator
                    = UserValidationHelper.getSpecialUserValidator(chatroomId);
            hasExtendedPermissions = validator.handle(cu);
        } catch (UserNotAllowedException e) {
            hasExtendedPermissions = false;
        }

        if (oldestAdditionTimestamp == null && oldestAdditionId == null) {

            return userPagedQueryHandler.handleQuery(
                    new PageQuery.GetPage<>(chatroomId,
                            new ChatroomUserPageData(
                                    null,
                                    null,
                                    hasExtendedPermissions)));

        } else if (oldestAdditionTimestamp != null && oldestAdditionId != null) {

            return userPagedQueryHandler.handleQuery(
                    new PageQuery.GetPage<>(
                            chatroomId,
                            new ChatroomUserPageData(oldestAdditionTimestamp,
                            oldestAdditionId,
                            hasExtendedPermissions)));

        } else {
            throw new PaginationException("");
        }
    }

    @Override
    public int addUsers(long requestingUserId,
                        ChatroomUsersSaveData saveData) {

        ChatroomUserDetails cu = getUserDetailsForSelf(requestingUserId, saveData.chatroomId());

        ValidationHandler<ChatroomUserDetails> validator
                = UserValidationHelper.getSpecialUserValidator(
                saveData.chatroomId());

        validator.handle(cu);

        Set<Long> friendSet = friendServiceApiClient.getAllFriendsOfPerson(
                requestingUserId)
                .stream()
                .map(PersonDto::id)
                .collect(Collectors.toSet());

        saveData.userToRole().keySet().forEach(userId -> {
            if(!friendSet.contains(userId)) {
                throw new UserNotAllowedException("");
            }
        });

        return commandHandler.handleCommand(
                new ChatroomUserCommand.AddUsers(saveData));

    }

    @Override
    public int changeUserRole(long requestingUserId,
                              int chatroomId,
                              long affectedUserId,
                              String updatedRole) {

        ChatroomUserDetails cu = getUserDetailsForSelf(requestingUserId, chatroomId);
        ChatroomRole role = ChatroomRole.valueOf(updatedRole.toUpperCase());

        ValidationHandler<ChatroomUserDetails> validator =
                UserValidationHelper.getSpecialUserValidator(chatroomId);

        validator.handle(cu);

        return commandHandler.handleCommand(new ChatroomUserCommand
                .ChangeUserRole(affectedUserId, role));

    }

    @Override
    public int changeUserMembershipStatus(long requestingUserId,
                                          int chatroomId,
                                          long affectedUserId,
                                          String updatedStatus) {

        ChatroomUserDetails requestingCu = getUserDetailsForSelf(requestingUserId, chatroomId);
        MembershipStatus updatedStatusEnum = MembershipStatus.valueOf(updatedStatus.toUpperCase());

        ValidationHandler<ChatroomUserDetails> validator =
                UserValidationHelper.getSpecialUserValidator(chatroomId);

        validator.handle(requestingCu);

        if (requestingUserId == affectedUserId) {

            return commandHandler.handleCommand(
                    new ChatroomUserCommand.ChangeMembershipStatus(
                            requestingCu.id(),
                            updatedStatusEnum));

        } else {

            ChatroomUserDetails cuAffected = getUserDetailsForRequester(
                    requestingUserId, affectedUserId, chatroomId);

            validator = UserValidationHelper
                    .getUserChatroomPresenceValidator(chatroomId);

            validator.handle(cuAffected);

            return commandHandler.handleCommand(
                    new ChatroomUserCommand.ChangeMembershipStatus(
                            cuAffected.userId(),
                            updatedStatusEnum));

        }
    }

    @Override
    public int addFirstChatroomUser(long userId, int chatroomId) {

        return commandHandler.handleCommand(
                new ChatroomUserCommand.AddUsers(
                        new ChatroomUsersSaveData(
                                chatroomId,
                                Map.of(userId, ChatroomRole.ADMIN)
                        )));

    }

    @Override
    public ChatroomUserDetails getUserDetailsForSelf(long requestingUserId, int chatroomId) {

        return this.userQuery.handleQuery(new DetailQuery.GetData<>(new Pair<>(requestingUserId, chatroomId)));

    }

    @Override
    public ChatroomUserDetails getUserDetailsForRequester(long requestingUserId, long userId, int chatroomId) {

        ChatroomUserDetails requestingCu = this.userQuery.handleQuery(
                new DetailQuery.GetData<>(new Pair<>(requestingUserId, chatroomId)));

        ChatroomUserDetails cu = this.userQuery.handleQuery(
                new DetailQuery.GetData<>(new Pair<>(userId, chatroomId)));

        UserValidationHelper.getSameChatroomUsersValidator(cu).handle(requestingCu);

        return cu;

    }


}
