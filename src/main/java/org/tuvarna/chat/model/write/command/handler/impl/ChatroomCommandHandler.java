package org.tuvarna.chat.model.write.command.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.exceptions.persistence.missing.ChatroomMissingException;
import org.tuvarna.chat.model.entity.postgres.Chatroom;
import org.tuvarna.chat.model.write.command.ChatroomCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.repository.ChatroomsWrite;

@ApplicationScoped
@Named("ChatroomCommandHandler")
public class ChatroomCommandHandler implements CommandHandler<Integer, ChatroomCommand> {

    final Logger logger = LoggerFactory.getLogger(ChatroomCommandHandler.class);

    ChatroomsWrite chatroomWrite;

    @Inject
    public ChatroomCommandHandler(ChatroomsWrite chatroomWrite) {
        this.chatroomWrite = chatroomWrite;
    }

    @Override
    public Integer handleCommand(ChatroomCommand command) {
        switch (command) {

            case ChatroomCommand.CreateChatroom(String name) -> {

                Chatroom c = new Chatroom();
                c.setName(name);

                c = chatroomWrite.save(c);

                if (c == null) {
                    throw new ChatroomMissingException("Could not create chatroom = {" + name + "}");
                }

                return c.getId();

            }

            case ChatroomCommand.ArchiveChatroom(int chatroomId) -> {

                return chatroomWrite.archiveChatroomById(chatroomId);

            }
        }
    }
}

