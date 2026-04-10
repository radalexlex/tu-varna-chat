package org.tuvarna.chat.model.write.command;

import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;

import java.util.List;

public sealed interface ChatMessagePersistenceCommand permits
        ChatMessagePersistenceCommand.SendMessages {

    record SendMessages(List<ChatMessageOperationalData> requestList) implements ChatMessagePersistenceCommand {
    }

}
