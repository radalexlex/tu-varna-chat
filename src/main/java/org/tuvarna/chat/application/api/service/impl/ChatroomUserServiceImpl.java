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
import org.tuvarna.chat.application.exceptions.base.ApplicationException;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.persistence.missing.ChatroomUserMissingException;
import org.tuvarna.chat.application.exceptions.service.ChatroomUserServiceException;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class ChatroomUserServiceImpl implements ChatroomUserService {

    private static final Logger log = LoggerFactory.getLogger(ChatroomUserServiceImpl.class);

    QueryHandler<ChatroomUserDetails, DetailQuery<Pair<Long, Integer>>> userQuery;
    QueryHandler<ContentPage<ChatroomUserDetails>,
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
                                   QueryHandler<ChatroomUserDetails, DetailQuery<Pair<Long, Integer>>> userQuery) {
        this.userPagedQueryHandler = userPagedQueryHandler;
        this.commandHandler = commandHandler;
        this.userQuery = userQuery;

    }

    @Override
    public ContentPage<ChatroomUserDetails> getUsers(long requestingUserId,
                                                     int chatroomId,
                                                     Instant oldestAdditionTimestamp,
                                                     Integer oldestAdditionId) {
        try {

            ChatroomUserDetails cu = getUserDetailsForSelf(requestingUserId, chatroomId);

            boolean hasExtendedPermissions;

            try {
                ValidationHandler<ChatroomUserDetails> validator =
                        UserValidationHelper.getSpecialUserValidator(chatroomId);
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

            } else if (oldestAdditionTimestamp != null
                    && oldestAdditionId != null) {

                return userPagedQueryHandler.handleQuery(
                        new PageQuery.GetPage<>(
                                chatroomId,
                                new ChatroomUserPageData(
                                        oldestAdditionTimestamp,
                                        oldestAdditionId,
                                        hasExtendedPermissions)));

            } else {
                throw new PaginationException("Invalid pagination parameters");
            }

        } catch (ApplicationException e) {
            throw new ChatroomUserServiceException(e);
        }
    }

    @Override
    public int addUsers(long requestingUserId,
                        ChatroomUsersSaveData saveData) {
        try {

            ChatroomUserDetails cu =
                    getUserDetailsForSelf(requestingUserId, saveData.chatroomId());

            ValidationHandler<ChatroomUserDetails> validator =
                    UserValidationHelper.getSpecialUserValidator(saveData.chatroomId());

            validator.handle(cu);

            Set<Long> friendSet = friendServiceApiClient
                    .getAllFriendsOfPerson(requestingUserId)
                    .stream()
                    .map(PersonDto::id)
                    .collect(Collectors.toSet());

            saveData.userToRole().keySet().forEach(userId -> {
                if (!friendSet.contains(userId)) {
                    throw new UserNotAllowedException(
                            "User " + userId + " is not a friend"
                    );
                }
            });

            return commandHandler.handleCommand(
                    new ChatroomUserCommand.AddUsers(saveData));

        } catch (ApplicationException e) {
            throw new ChatroomUserServiceException(e);
        }
    }

    @Override
    public int changeUserRole(long requestingUserId,
                              int chatroomId,
                              long affectedUserId,
                              String updatedRole) {
        try {

            ChatroomUserDetails cu =
                    getUserDetailsForSelf(requestingUserId, chatroomId);

            ChatroomRole role = ChatroomRole.valueOf(updatedRole.toUpperCase());

            ValidationHandler<ChatroomUserDetails> validator =
                    UserValidationHelper.getSpecialUserValidator(chatroomId);

            validator.handle(cu);

            int updated = commandHandler.handleCommand(
                    new ChatroomUserCommand.ChangeUserRole(affectedUserId, role));

            if (updated == 0) {
                throw new ChatroomUserMissingException(
                        "User " + affectedUserId + " not found in chatroom " + chatroomId);
            }

            return updated;

        } catch (ApplicationException e) {
            throw new ChatroomUserServiceException(e);
        }
    }

    @Override
    public int changeUserMembershipStatus(long requestingUserId,
                                          int chatroomId,
                                          long affectedUserId,
                                          String updatedStatus) {
        try {

            ChatroomUserDetails requestingCu =
                    getUserDetailsForSelf(requestingUserId, chatroomId);

            MembershipStatus updatedStatusEnum =
                    MembershipStatus.valueOf(updatedStatus.toUpperCase());

            ValidationHandler<ChatroomUserDetails> validator =
                    UserValidationHelper.getSpecialUserValidator(chatroomId);

            validator.handle(requestingCu);

            int updated;

            if (requestingUserId == affectedUserId) {

                updated = commandHandler.handleCommand(
                        new ChatroomUserCommand.ChangeMembershipStatus(
                                requestingCu.id(),
                                updatedStatusEnum));

            } else {

                ChatroomUserDetails cuAffected =
                        getUserDetailsForRequester(requestingUserId, affectedUserId, chatroomId);

                UserValidationHelper
                        .getUserChatroomPresenceValidator(chatroomId)
                        .handle(cuAffected);

                updated = commandHandler.handleCommand(
                        new ChatroomUserCommand.ChangeMembershipStatus(
                                cuAffected.userId(),
                                updatedStatusEnum));
            }

            if (updated == 0) {
                throw new ChatroomUserMissingException(
                        "User " + affectedUserId + " not found");
            }

            return updated;

        } catch (ApplicationException e) {
            throw new ChatroomUserServiceException(e);
        }
    }

    @Override
    public int addFirstChatroomUser(long userId, int chatroomId) {

        try {
            return commandHandler.handleCommand(
                    new ChatroomUserCommand.AddUsers(
                            new ChatroomUsersSaveData(
                                    chatroomId,
                                    Map.of(userId, ChatroomRole.ADMIN)
                            )));
        } catch (ApplicationException e) {
            throw new ChatroomUserServiceException(e);
        }

    }

    @Override
    public ChatroomUserDetails getUserDetailsForSelf(long requestingUserId, int chatroomId) {

        try {
            return this.userQuery.handleQuery(new DetailQuery.GetData<>(
                    new Pair<>(requestingUserId, chatroomId)));
        } catch (ApplicationException e) {
            throw new ChatroomUserServiceException(e);
        }

    }

    @Override
    public ChatroomUserDetails getUserDetailsForRequester(long requestingUserId, long userId, int chatroomId) {

        try {

            ChatroomUserDetails requestingCu = this.userQuery.handleQuery(
                    new DetailQuery.GetData<>(new Pair<>(requestingUserId, chatroomId)));

            ChatroomUserDetails cu = this.userQuery.handleQuery(
                    new DetailQuery.GetData<>(new Pair<>(userId, chatroomId)));

            UserValidationHelper.getSameChatroomUsersValidator(cu).handle(requestingCu);

            return cu;
        } catch (ApplicationException e) {
            throw new ChatroomUserServiceException(e);
        }


    }

    @Override
    public int updateLastReadStatus(long requestingUserId,
                                    int chatroomId,
                                    long lastReadMessage) {
        try {

            ChatroomUserDetails cu =
                    getUserDetailsForSelf(requestingUserId, chatroomId);

            return commandHandler.handleCommand(
                    new ChatroomUserCommand.UpdateReadStatus(
                            cu.userId(),
                            lastReadMessage));

        } catch (ApplicationException e) {
            throw new ChatroomUserServiceException(e);
        }
    }


}
