package org.tuvarna.chat.application.api.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.application.api.service.ChatroomUserService;
import org.tuvarna.chat.application.api.service.validation.UserValidationHelper;
import org.tuvarna.chat.application.exceptions.base.ApplicationException;
import org.tuvarna.chat.application.exceptions.persistence.DataPersistenceException;
import org.tuvarna.chat.application.exceptions.persistence.missing.ChatMessageMissingException;
import org.tuvarna.chat.application.exceptions.service.ChatMessageServiceException;
import org.tuvarna.chat.application.exceptions.validation.message.EmptyMessageContentException;
import org.tuvarna.chat.application.exceptions.validation.message.MessageTooLongException;
import org.tuvarna.chat.application.exceptions.validation.room.InvalidChatroomIdException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageServiceImpl.class);
    QueryHandler<ContentPage<ChatMessageElement>, PageQuery<Integer, ChatMessagePageData>> messageQueryHandler;
    CommandHandler<Integer, ChatMessageMutationCommand> mutationCommandHandler;
    CommandHandler<MessagePersistenceStatus, ChatMessagePersistenceCommand> persistenceCommandHandler;
    ChatroomUserService chatroomUserService;

    @Inject
    public ChatMessageServiceImpl(@Named("ChatMessagePageQueryHandler")
                                  QueryHandler<ContentPage<ChatMessageElement>, PageQuery<Integer, ChatMessagePageData>>
                                          messageQueryHandler,
                                  @Named("ChatMessagePersistenceCommandHandler")
                                  CommandHandler<MessagePersistenceStatus, ChatMessagePersistenceCommand> persistenceCommandHandler,
                                  @Named("ChatMessageMutationCommandHandler")
                                  CommandHandler<Integer, ChatMessageMutationCommand> mutationCommandHandler,
                                  ChatroomUserService chatroomUserService) {
        this.messageQueryHandler = messageQueryHandler;
        this.mutationCommandHandler = mutationCommandHandler;
        this.persistenceCommandHandler = persistenceCommandHandler;
        this.chatroomUserService = chatroomUserService;

    }

    @Override
    public Map<AckStatus, List<ChatMessageOperationalData>> addMessages(
            List<ChatMessageOperationalData> saveData) {

        try {
            if (saveData == null || saveData.isEmpty()) {
                throw new EmptyMessageContentException(
                        "Message list cannot be null or empty"
                );
            }

            MessagePersistenceStatus status;

            status = persistenceCommandHandler
                    .handleCommand(new ChatMessagePersistenceCommand
                            .SendMessages(saveData));

            if (status.errorIndexes().length != saveData.size()) {
                throw new DataPersistenceException("Mismatch between " +
                        "input and result");
            }

            if (!status.errored()) {
                return Map.of(
                        AckStatus.SUCCESS, saveData,
                        AckStatus.FAILURE, new ArrayList<>()
                );
            }

            Map<AckStatus, List<ChatMessageOperationalData>> result = Map.of(
                    AckStatus.SUCCESS, new ArrayList<>(),
                    AckStatus.FAILURE, new ArrayList<>()
            );

            boolean[] indexes = status.errorIndexes();

            for (int i = 0; i < indexes.length; i++) {
                if (indexes[i]) {
                    result.get(AckStatus.FAILURE).add(saveData.get(i));
                } else {
                    result.get(AckStatus.SUCCESS).add(saveData.get(i));
                }
            }

            return result;

        } catch (ApplicationException e) {
            throw new ChatMessageServiceException(e);
        }
    }

    @Override
    public int archiveMessage(long requestingUserId,
                              ChatMessageElement message,
                              int chatroom) {

        try {
            if (chatroom <= 0) {

                throw new InvalidChatroomIdException(
                        "Invalid chatroomId: " + chatroom
                );
            }

            ChatroomUserDetails req =
                    chatroomUserService.getUserDetailsForSelf(requestingUserId, chatroom);

            UserValidationHelper.getUserChatroomPresenceValidator(chatroom).handle(req);

            boolean isSuper;

            try {
                isSuper = UserValidationHelper
                        .getSpecialUserValidator(chatroom)
                        .handle(req);
            } catch (UserNotAllowedException e) {
                log.warn(e.getMessage());
                isSuper = false;
            }

            if (isSuper || (requestingUserId == message.senderId())) {
                int updated = mutationCommandHandler.handleCommand(
                        new ChatMessageMutationCommand.ArchiveMessageMutation(
                                message.id())
                );

                if (updated == 0) {
                    throw new ChatMessageMissingException(
                            "Message " + message.id() + " not found or already archived"
                    );
                }

                return updated;
            }

            throw new UserNotAllowedException(
                    "User " + requestingUserId +
                            " is not allowed to archive message " + message.id() +
                            " in chatroom " + chatroom
            );
        } catch (ApplicationException e) {
            throw new ChatMessageServiceException(e);
        }
    }

    @Override
    public int updateMessage(long requestingUserId,
                             ChatMessageElement message,
                             int chatroomId,
                             String newContent) {
        try {
            if (newContent == null || newContent.isBlank()) {
                throw new ChatMessageServiceException(new EmptyMessageContentException(
                        "Message content cannot be null or empty"
                ));
            }

            if (newContent.length() > 5000) {
                throw new ChatMessageServiceException(new MessageTooLongException(
                        "Message content exceeds maximum allowed length " +
                                "(5000 characters)"
                ));
            }

            ChatroomUserDetails req = chatroomUserService.getUserDetailsForSelf(requestingUserId, chatroomId);

            if (req.userId() != message.senderId()) {
                throw new ChatMessageServiceException(
                        new UserNotAllowedException("User is not allowed to" +
                                " update this message")
                );
            }

            UserValidationHelper.getUserChatroomPresenceValidator(chatroomId).handle(req);

            int updated = mutationCommandHandler.handleCommand(
                    new ChatMessageMutationCommand.UpdateMessageMutation(
                            message.id(),
                            newContent
                    )
            );

            if (updated == 0) {
                throw new ChatMessageMissingException(
                        "Message " + message.id() + " not found or already deleted"
                );
            }

            return updated;
        } catch (ApplicationException e) {
            throw new ChatMessageServiceException(e);
        }
    }

    @Override
    public ContentPage<ChatMessageElement> getMessagePage(long requestingUserId,
                                                          int chatroomId,
                                                          Long messageCursorId,
                                                          boolean downScroll,
                                                          boolean initialRequest) {
        try {

            ChatroomUserDetails cu =
                    chatroomUserService
                            .getUserDetailsForSelf(
                                    requestingUserId,
                                    chatroomId);

            UserValidationHelper.getUserChatroomPresenceValidator(chatroomId).handle(cu);

            if (messageCursorId == null) { // fallback
                return messageQueryHandler.handleQuery(
                        new PageQuery.GetPage<>(
                                chatroomId,
                                null));
            } else {
                return messageQueryHandler.handleQuery(
                        new PageQuery.GetPage<>(
                                chatroomId,
                                new ChatMessagePageData(
                                        messageCursorId,
                                        downScroll,
                                        initialRequest))
                );
            }
        } catch (ApplicationException e) {
            throw new ChatMessageServiceException(e);
        }
    }
}
