package org.tuvarna.chat.application.api.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.application.api.service.ChatroomUserService;
import org.tuvarna.chat.application.api.service.validation.UserValidationHelper;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatMessagePageData;
import org.tuvarna.chat.model.write.command.ChatMessageCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.dto.ChatMessageSaveData;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    QueryHandler<ContentPage<ChatMessageElement>, PageQuery<Integer, ChatMessagePageData>> messageQueryHandler;
    CommandHandler<Integer, ChatMessageCommand> commandHandler;
    ChatroomUserService chatroomUserService;

    @Inject
    public ChatMessageServiceImpl(@Named("ChatMessagePageQueryHandler")
                              QueryHandler<ContentPage<ChatMessageElement>, PageQuery<Integer, ChatMessagePageData>>
                                      messageQueryHandler,
                                  @Named("ChatMessageCommandHandler")
                              CommandHandler<Integer, ChatMessageCommand> commandHandler,
                                  ChatroomUserService chatroomUserService) {
        this.messageQueryHandler = messageQueryHandler;
        this.commandHandler = commandHandler;
        this.chatroomUserService = chatroomUserService;
    }

    @Override
    public int addMessages(List<ChatMessageSaveData> saveData) {

        return commandHandler.handleCommand(new ChatMessageCommand
                .SendMessages(saveData));

    }

    @Override
    public int archiveMessage(long requestingUserId, ChatMessageElement message) {
        //TODO: check requesting user first

        ChatroomUserDetails cu = chatroomUserService.getUserDetailsForSelf(
                message.senderUser()); // TODO: rewrite logic of validation, don't believe the outer data, believe DB
        boolean isSuper;

        try {
            isSuper = UserValidationHelper.getSpecialUserValidator(
                    cu.chatroomId()).handle(cu);
        } catch (UserNotAllowedException e) {
            isSuper = false;
        }

        if(isSuper || requestingUserId == message.senderUser()) {
            return commandHandler.handleCommand(new ChatMessageCommand.ArchiveMessage(message.id()));
        } else {
            throw new UserNotAllowedException("");
        }

    }

    @Override
    public int updateMessage(long requestingUserId, ChatMessageElement message, String newContent) {
        //TODO: check requesting user first

        ChatroomUserDetails cu = chatroomUserService.getUserDetailsForSelf(
                message.senderUser()); // TODO: rewrite logic of validation, don't believe the outer data, believe DB

        if(requestingUserId != message.senderUser()) {
            throw new UserNotAllowedException("");
        }

        UserValidationHelper.getUserChatroomPresenceValidator(cu.chatroomId()).handle(cu);

        return commandHandler.handleCommand(new ChatMessageCommand.UpdateMessage(message.id(), newContent));
    }

    @Override
    public ContentPage<ChatMessageElement> getMessagePage(long requestingUserId, int chatroomId, Instant oldestTimestamp, Integer oldestId) {

        ChatroomUserDetails cu = chatroomUserService.getUserDetailsForSelf()

        if(oldestTimestamp == null && oldestId == null) {
            return messageQueryHandler.handleQuery(
                    new PageQuery.GetPage<>(
                            chatroomId,
                            null));

        } else if(oldestTimestamp != null && oldestId != null) {
            return messageQueryHandler.handleQuery(
                    new PageQuery.GetPage<>(
                            chatroomId,
                            new ChatMessagePageData(
                                    oldestTimestamp,
                                    oldestId))
            );
        } else {
            throw new PaginationException("");
        }

    }

}
