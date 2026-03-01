package org.tuvarna.chat.application.api.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import org.tuvarna.chat.application.api.service.validation.UserValidationHelper;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.DetailQuery;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatroomEventfulPageData;
import org.tuvarna.chat.model.write.command.ChatroomCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
@Transactional
public class ChatroomServiceImpl implements ChatroomService {

    QueryHandler<ChatroomOverview, DetailQuery<Integer>> roomQueryHandler;
    QueryHandler<ContentPage<ChatroomEventfulElement>, PageQuery<Long, ChatroomEventfulPageData>> roomEventfulQueryHandler;
    CommandHandler<Integer, ChatroomCommand> commandHandler;

    ChatroomUserService chatroomUserService;

    @Inject
    public ChatroomServiceImpl(@Named("ChatroomQueryHandler")
                           QueryHandler<ChatroomOverview, DetailQuery<Integer>> roomQueryHandler,
                               @Named("ChatroomEventfulPageQueryHandler")
                           QueryHandler<ContentPage<ChatroomEventfulElement>, PageQuery<Long, ChatroomEventfulPageData>> roomEventfulQueryHandler,
                               @Named("ChatroomCommandHandler")
                           CommandHandler<Integer, ChatroomCommand> commandHandler,
                               ChatroomUserService chatroomUserService,
                               UserValidationHelper userValidationHelper) {
        this.roomQueryHandler = roomQueryHandler;
        this.roomEventfulQueryHandler = roomEventfulQueryHandler;
        this.commandHandler = commandHandler;
        this.chatroomUserService = chatroomUserService; // not an interface
    }

    @Override
    public int createChatroom(long adminId, String name) {

        int newRoomId = commandHandler.handleCommand(new ChatroomCommand.CreateChatroom(name));

        return chatroomUserService.addFirstChatroomUser(adminId, newRoomId);

    }

    @Override
    public int archiveChatroom(long userId, int chatroomId) {

        UserValidationHelper.getSpecialUserValidator(chatroomId)
                .handle(chatroomUserService.getUserDetailsForSelf(userId));

        return commandHandler.handleCommand(new ChatroomCommand
                .ArchiveChatroom(chatroomId));

    }

    // I assume that the chatroomUserId passed is a valid chatroomUserId securely passed from the upper services
    @Override
    public ContentPage<ChatroomEventfulElement> getChatroomEventfulElements(long userId,
                                                                     Instant latestEventTimeOnPage,
                                                                     Integer latestChatroomIdOnPage,
                                                                     Long latestChatMessageIdOnPage) {

        if (latestChatMessageIdOnPage == null
                && latestChatroomIdOnPage == null
                && latestEventTimeOnPage == null) {

            return roomEventfulQueryHandler.handleQuery(
                    new PageQuery.GetPage<>(userId, null));

        } else if (latestChatMessageIdOnPage != null
                && latestChatroomIdOnPage != null
                && latestEventTimeOnPage != null) {

            return roomEventfulQueryHandler.handleQuery(
                    new PageQuery.GetPage<>(
                            userId,
                            new ChatroomEventfulPageData(
                            latestEventTimeOnPage,
                            latestChatroomIdOnPage,
                            latestChatMessageIdOnPage)));

        } else {
            throw new PaginationException("");
        }

    }

    @Override
    public ChatroomOverview getChatroomOverview(long requestingUserId, int chatroomId) {

        ChatroomUserDetails cu =
                chatroomUserService.getUserDetailsForSelf(requestingUserId);

        UserValidationHelper.getUserChatroomPresenceValidator(chatroomId).handle(cu);

        return roomQueryHandler.handleQuery(
                new DetailQuery.GetData<>(chatroomId));

    }


}
