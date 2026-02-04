package org.tuvarna.chat.application.api.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;
import org.tuvarna.chat.model.read.query.ChatroomEventfulPagedQuery;
import org.tuvarna.chat.model.read.query.ChatroomQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.write.command.ChatroomCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;

import java.util.List;

@ApplicationScoped
public class ChatroomService {

    QueryHandler<ChatroomOverview, ChatroomQuery> roomQueryHandler;

    QueryHandler<List<ChatroomEventfulElement>, ChatroomEventfulPagedQuery> roomEventfulQueryHandler;

    CommandHandler<Integer, ChatroomCommand> commandHandler;

    public ChatroomService(@Named("ChatroomQueryHandler")
                           QueryHandler<ChatroomOverview, ChatroomQuery> roomQueryHandler,
                           @Named("ChatroomEventfulQueryHandler")
                           QueryHandler<List<ChatroomEventfulElement>, ChatroomEventfulPagedQuery> roomEventfulQueryHandler,
                           @Named("ChatroomCommandHandler")
                           CommandHandler<Integer, ChatroomCommand> commandHandler) {
        this.roomQueryHandler = roomQueryHandler;
        this.roomEventfulQueryHandler = roomEventfulQueryHandler;
        this.commandHandler = commandHandler;
    }




}
