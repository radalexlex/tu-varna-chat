package org.tuvarna.chat.model.write.command;

import org.tuvarna.chat.model.write.command.ChatMessageCommand.ArchiveMessage;
import org.tuvarna.chat.model.write.command.ChatMessageCommand.SendMessages;
import org.tuvarna.chat.model.write.command.ChatMessageCommand.UpdateMessage;
import org.tuvarna.chat.model.write.dto.ChatMessageSaveData;

import java.util.List;

public sealed interface ChatMessageCommand permits
        ChatMessageCommand.ArchiveMessage,
        ChatMessageCommand.UpdateMessage,
        ChatMessageCommand.SendMessages {

    record SendMessages(List<ChatMessageSaveData> requestList) implements ChatMessageCommand {
    }

    record ArchiveMessage(int messageId) implements ChatMessageCommand {
    }

    record UpdateMessage(int messageId, String updatedContent) implements ChatMessageCommand {
    }

}
