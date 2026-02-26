package org.tuvarna.chat.model.write.command;

public sealed interface ChatroomCommand permits
        ChatroomCommand.CreateChatroom,
        ChatroomCommand.ArchiveChatroom {

    record CreateChatroom(String name) implements ChatroomCommand {
    }

    record ArchiveChatroom(int chatroomId) implements ChatroomCommand {
    }

}
