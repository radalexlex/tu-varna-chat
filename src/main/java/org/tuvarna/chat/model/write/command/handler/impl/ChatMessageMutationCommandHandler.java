package org.tuvarna.chat.model.write.command.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.model.write.command.ChatMessageMutationCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.repository.ChatMessagesWrite;

@ApplicationScoped
@Named("ChatMessageMutationCommandHandler")
public class ChatMessageMutationCommandHandler implements CommandHandler<Integer, ChatMessageMutationCommand> {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageMutationCommandHandler.class);
    ChatMessagesWrite chatMessagesWrite;

    @Inject
    public ChatMessageMutationCommandHandler(ChatMessagesWrite chatMessageWrite) {
        this.chatMessagesWrite = chatMessageWrite;
    }

    @Override
    public Integer handleCommand(ChatMessageMutationCommand command) {
        switch (command) {
            case ChatMessageMutationCommand.ArchiveMessageMutation(
                    long messageId
            ) -> {

                return chatMessagesWrite.archiveMessageById(messageId);


            }
            case ChatMessageMutationCommand.UpdateMessageMutation(
                    long messageId,
                    String updatedContent
            ) -> {


                return chatMessagesWrite.updateMessageById(messageId, updatedContent);


            }
        }
    }
}
