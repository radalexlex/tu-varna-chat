package org.tuvarna.chat.application.api.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.tuvarna.chat.application.api.service.validation.UserValidationHelper;
import org.tuvarna.chat.application.api.service.validation.ValidationHandler;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.ChatroomUserPagedQuery;
import org.tuvarna.chat.model.read.query.ChatroomUserQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.write.command.ChatroomUserCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.dto.ChatroomUsersSaveData;

import java.time.Instant;
import java.util.Map;

@ApplicationScoped
@Transactional
public class ChatroomUserService {

    QueryHandler<ChatroomUserDetails, ChatroomUserQuery> userQuery;

    QueryHandler<ContentPage<ChatroomUserDetails>, ChatroomUserPagedQuery> userPagedQueryHandler;

    CommandHandler<Integer, ChatroomUserCommand> commandHandler;

    public ChatroomUserService(@Named("ChatroomUserPageQueryHandler")
                               QueryHandler<ContentPage<ChatroomUserDetails>, ChatroomUserPagedQuery> userPagedQueryHandler,
                               @Named("ChatroomUserCommandHandler")
                               CommandHandler<Integer, ChatroomUserCommand> commandHandler,
                               @Named("ChatroomUserQueryHandler")
                               QueryHandler<ChatroomUserDetails, ChatroomUserQuery> userQuery) {
        this.userPagedQueryHandler = userPagedQueryHandler;
        this.commandHandler = commandHandler;
        this.userQuery = userQuery;

    }

    public ContentPage<ChatroomUserDetails> getUsers(long requestingUserId,
                                                     int chatroomId,
                                                     Instant oldestAdditionTimestamp,
                                                     Integer oldestAdditionId) {

        ChatroomUserDetails cu = getUserDetailsForSelf(requestingUserId);

        boolean hasExtendedPermissions;

        try {
            ValidationHandler<ChatroomUserDetails> validator
                    = UserValidationHelper.getSpecialUserValidator(chatroomId);
            hasExtendedPermissions = validator.handle(cu);
        } catch (UserNotAllowedException e) {
            // log about attempt
            hasExtendedPermissions = false;
        }

        if (oldestAdditionTimestamp == null && oldestAdditionId == null) {

            return userPagedQueryHandler.handleQuery(
                    new ChatroomUserPagedQuery.GetFirstPage(
                            chatroomId, hasExtendedPermissions));

        } else if (oldestAdditionTimestamp != null && oldestAdditionId != null) {

            return userPagedQueryHandler.handleQuery(
                    new ChatroomUserPagedQuery.GetFollowingPage(
                            chatroomId,
                            oldestAdditionTimestamp,
                            oldestAdditionId,
                            hasExtendedPermissions));

        } else {
            throw new PaginationException("");
        }
    }

    public int addUsers(long requestingUserId,
                        ChatroomUsersSaveData saveData) {

        ChatroomUserDetails cu = getUserDetailsForSelf(requestingUserId);

        ValidationHandler<ChatroomUserDetails> validator
                = UserValidationHelper.getSpecialUserValidator(
                saveData.chatroomId());

        validator.handle(cu);

        return commandHandler.handleCommand(
                new ChatroomUserCommand.AddUsers(saveData));

    }

    public int changeUserRole(long requestingUserId,
                              int chatroomId,
                              long affectedUserId,
                              String updatedRole) {

        ChatroomUserDetails cu = getUserDetailsForSelf(requestingUserId);
        ChatroomRole role = ChatroomRole.valueOf(updatedRole.toUpperCase());

        ValidationHandler<ChatroomUserDetails> validator =
                UserValidationHelper.getSpecialUserValidator(chatroomId);

        validator.handle(cu);

        return commandHandler.handleCommand(new ChatroomUserCommand
                .ChangeUserRole(affectedUserId, role));

    }

    public int changeUserMembershipStatus(long requestingUserId,
                                          int chatroomId,
                                          long affectedUserId,
                                          String updatedStatus) {

        ChatroomUserDetails requestingCu = getUserDetailsForSelf(requestingUserId);
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
                    requestingUserId, affectedUserId);

            validator = UserValidationHelper
                    .getUserChatroomPresenceValidator(chatroomId);

            validator.handle(cuAffected);

            return commandHandler.handleCommand(
                    new ChatroomUserCommand.ChangeMembershipStatus(
                            cuAffected.userId(),
                            updatedStatusEnum));

        }
    }

    public int addFirstChatroomUser(long userId, int chatroomId) {

        return commandHandler.handleCommand(
                new ChatroomUserCommand.AddUsers(
                        new ChatroomUsersSaveData(
                                chatroomId,
                                Map.of(userId, ChatroomRole.ADMIN)
                        )));

    }

    public ChatroomUserDetails getUserDetailsForSelf(long requestingUserId) {

        return this.userQuery.handleQuery(new ChatroomUserQuery
                .GetChatroomUserDetails(requestingUserId));

    }

    public ChatroomUserDetails getUserDetailsForRequester(long requestingUserId, long userId) {

        ChatroomUserDetails requestingCu = this.userQuery.handleQuery(
                new ChatroomUserQuery
                        .GetChatroomUserDetails(requestingUserId));

        ChatroomUserDetails cu = this.userQuery.handleQuery(
                new ChatroomUserQuery
                        .GetChatroomUserDetails(userId));

        UserValidationHelper.getSameChatroomUsersValidator(cu).handle(requestingCu);

        return cu;

    }


}
