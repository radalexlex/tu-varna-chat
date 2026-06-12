package org.tuvarna.chat.model.write.command;

public sealed interface ChatroomCommand permits
        ChatroomCommand.CreateChatroom,
        ChatroomCommand.ArchiveChatroom,
        ChatroomCommand.UpdateChatroomName {

    record CreateChatroom(String name) implements ChatroomCommand {
    }

    record ArchiveChatroom(int chatroomId) implements ChatroomCommand {
    }

    record UpdateChatroomName(int chatroomId, String newName) implements ChatroomCommand {
    }

}
