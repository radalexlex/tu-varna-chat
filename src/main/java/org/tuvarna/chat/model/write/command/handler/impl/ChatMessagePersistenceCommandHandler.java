package org.tuvarna.chat.model.write.command.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.model.write.command.ChatMessagePersistenceCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;
import org.tuvarna.chat.model.write.dto.MessagePersistenceStatus;
import org.tuvarna.chat.model.write.repository.ChatMessagesWrite;

import java.util.List;

@ApplicationScoped
@Named("ChatMessagePersistenceCommandHandler")
public class ChatMessagePersistenceCommandHandler implements CommandHandler<MessagePersistenceStatus, ChatMessagePersistenceCommand> {

    ChatMessagesWrite chatMessagesWrite;

    @Inject
    public ChatMessagePersistenceCommandHandler(ChatMessagesWrite chatMessagesWrite) {
        this.chatMessagesWrite = chatMessagesWrite;
    }

    @Override
    public MessagePersistenceStatus handleCommand(ChatMessagePersistenceCommand command) {
        switch (command) {
            case ChatMessagePersistenceCommand.SendMessages(List<ChatMessageOperationalData> saveData) -> {
                return chatMessagesWrite.insertMessages(saveData);
            }
        }
    }

}
