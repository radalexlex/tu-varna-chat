package org.tuvarna.chat.model.write.command;

import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.write.command.ChatroomUserCommand.AddUsers;
import org.tuvarna.chat.model.write.command.ChatroomUserCommand.ChangeMembershipStatus;
import org.tuvarna.chat.model.write.command.ChatroomUserCommand.ChangeUserRole;
import org.tuvarna.chat.model.write.dto.ChatroomUsersSaveData;

public sealed interface ChatroomUserCommand permits
        ChatroomUserCommand.ChangeUserRole,
        ChatroomUserCommand.ChangeMembershipStatus,
        ChatroomUserCommand.AddUsers {

    record AddUsers(ChatroomUsersSaveData saveData) implements ChatroomUserCommand {
    }

    record ChangeUserRole(int joinId, ChatroomRole role) implements ChatroomUserCommand {
    }

    record ChangeMembershipStatus(int joinId, MembershipStatus status) implements ChatroomUserCommand {
    }

}
