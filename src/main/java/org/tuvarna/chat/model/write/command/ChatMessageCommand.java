package org.tuvarna.chat.model.write.command;

import org.tuvarna.chat.model.write.dto.ChatMessageSaveData;

import java.util.List;

public sealed interface ChatMessageCommand permits
        ChatMessageCommand.ArchiveMessage,
        ChatMessageCommand.UpdateMessage,
        ChatMessageCommand.SendMessages {

    record SendMessages(List<ChatMessageSaveData> requestList) implements ChatMessageCommand {
    }

    record ArchiveMessage(long messageId) implements ChatMessageCommand {
    }

    record UpdateMessage(long messageId, String updatedContent) implements ChatMessageCommand {
    }

}
