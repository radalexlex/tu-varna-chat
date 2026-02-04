package org.tuvarna.chat.application.api.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.ChatroomUserPagedQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.write.command.ChatroomUserCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;

@ApplicationScoped
public class ChatroomUserService {

    QueryHandler<ContentPage<ChatroomUserDetails>, ChatroomUserPagedQuery> userQueryHandler;

    CommandHandler<Integer, ChatroomUserCommand> commandHandler;

    public ChatroomUserService(@Named("ChatroomUserQueryHandler")
                               QueryHandler<ContentPage<ChatroomUserDetails>, ChatroomUserPagedQuery> userQueryHandler,
                               @Named("ChatroomUserCommandHandler")
                               CommandHandler<Integer, ChatroomUserCommand> commandHandler) {
        this.userQueryHandler = userQueryHandler;
        this.commandHandler = commandHandler;
    }
//
//    public ContentPage<ChatroomUserDetails> getUsers(int chatroomId,
//                                                     Instant oldestAdditionTimestamp,
//                                                     int oldestAdditionId) {
//
//
//
//        // some request code
//
//        // validation
//
//
//
//    }


}
