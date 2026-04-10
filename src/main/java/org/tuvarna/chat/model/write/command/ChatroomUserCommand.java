package org.tuvarna.chat.model.write.command;

import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.write.dto.ChatroomUsersSaveData;

public sealed interface ChatroomUserCommand permits
        ChatroomUserCommand.ChangeUserRole,
        ChatroomUserCommand.ChangeMembershipStatus,
        ChatroomUserCommand.AddUsers,
        ChatroomUserCommand.UpdateReadStatus {

    record AddUsers(ChatroomUsersSaveData saveData) implements ChatroomUserCommand {
    }

    record ChangeUserRole(long userId, ChatroomRole role) implements ChatroomUserCommand {
    }

    record ChangeMembershipStatus(long userId, MembershipStatus status) implements ChatroomUserCommand {
    }

    record UpdateReadStatus(long userId, Long newLastReadMessageId) implements ChatroomUserCommand {
    }

}
