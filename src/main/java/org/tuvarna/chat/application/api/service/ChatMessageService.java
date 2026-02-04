package org.tuvarna.chat.application.api.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.ChatMessagePagedQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.write.command.ChatMessageCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;

@ApplicationScoped
public class ChatMessageService {

    QueryHandler<ContentPage<ChatMessageElement>, ChatMessagePagedQuery> messageQueryHandler;

    CommandHandler<Integer, ChatMessageCommand> commandHandler;

//    public ChatMessageService() {}

    public ChatMessageService(@Named("ChatMessageQueryHandler")
                              QueryHandler<ContentPage<ChatMessageElement>, ChatMessagePagedQuery>
                                      messageQueryHandler,
                              @Named("ChatMessageCommandHandler")
                              CommandHandler<Integer, ChatMessageCommand> commandHandler) {
        this.messageQueryHandler = messageQueryHandler;
        this.commandHandler = commandHandler;
    }


}
