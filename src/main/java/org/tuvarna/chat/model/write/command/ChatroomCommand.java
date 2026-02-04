package org.tuvarna.chat.model.write.command;

import org.tuvarna.chat.model.write.command.ChatroomCommand.ArchiveChatroom;
import org.tuvarna.chat.model.write.command.ChatroomCommand.CreateChatroom;

public sealed interface ChatroomCommand permits
        ChatroomCommand.CreateChatroom,
        ChatroomCommand.ArchiveChatroom {

    record CreateChatroom(String name) implements ChatroomCommand {
    }

    record ArchiveChatroom(int chatroomId) implements ChatroomCommand {
    }

}
