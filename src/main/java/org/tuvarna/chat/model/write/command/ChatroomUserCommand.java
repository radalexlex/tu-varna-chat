package org.tuvarna.chat.model.write.command;

import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.write.dto.ChatroomUsersSaveData;

import java.time.Instant;

public sealed interface ChatroomUserCommand permits
        ChatroomUserCommand.ChangeUserRole,
        ChatroomUserCommand.ChangeMembershipStatus,
        ChatroomUserCommand.AddUsers,
        ChatroomUserCommand.UpdateLastReadById,
        ChatroomUserCommand.UpdateLastReadByTimestamp {

    record AddUsers(ChatroomUsersSaveData saveData) implements ChatroomUserCommand {
    }

    record ChangeUserRole(long userId, ChatroomRole role) implements ChatroomUserCommand {
    }

    record ChangeMembershipStatus(long userId, MembershipStatus status) implements ChatroomUserCommand {
    }

    record UpdateLastReadById(int chatroomId, long userId, long newLastRead) implements ChatroomUserCommand {
    }

    record UpdateLastReadByTimestamp(int chatroomId, long userId, Instant newLastReadTimestamp) implements ChatroomUserCommand {
    }

}
