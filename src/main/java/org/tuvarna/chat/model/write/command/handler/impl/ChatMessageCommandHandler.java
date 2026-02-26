package org.tuvarna.chat.model.write.command.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.model.write.command.ChatMessageCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.dto.ChatMessageSaveData;
import org.tuvarna.chat.model.write.repository.ChatMessagesWrite;

import java.util.List;

@ApplicationScoped
@Named("ChatMessageCommandHandler")
public class ChatMessageCommandHandler implements CommandHandler<Integer, ChatMessageCommand> {

    ChatMessagesWrite chatMessagesWrite;

    @Inject
    public ChatMessageCommandHandler(ChatMessagesWrite chatMessageWrite) {
        this.chatMessagesWrite = chatMessageWrite;
    }

    @Override
    public Integer handleCommand(ChatMessageCommand command) {
        switch (command) {
            case ChatMessageCommand.SendMessages(
                    List<ChatMessageSaveData> requestList
            ) -> {

                return chatMessagesWrite.insertMessages(requestList);

            }
            case ChatMessageCommand.ArchiveMessage(
                    long messageId
            ) -> {

                return chatMessagesWrite.archiveMessageById(messageId);

            }
            case ChatMessageCommand.UpdateMessage(
                    long messageId,
                    String updatedContent
            ) -> {

                return chatMessagesWrite.updateMessageById(messageId, updatedContent);

            }
        }
    }
}
