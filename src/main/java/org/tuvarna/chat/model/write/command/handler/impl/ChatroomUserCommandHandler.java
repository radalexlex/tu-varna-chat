package org.tuvarna.chat.model.write.command.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.write.command.ChatroomUserCommand;
import org.tuvarna.chat.model.write.command.handler.CommandHandler;
import org.tuvarna.chat.model.write.dto.ChatroomUsersSaveData;
import org.tuvarna.chat.model.write.repository.ChatroomUsersWrite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Named("ChatroomUserCommandHandler")
public class ChatroomUserCommandHandler implements CommandHandler<Integer, ChatroomUserCommand> {

    ChatroomUsersWrite chatroomUsersWrite;

    public ChatroomUserCommandHandler() {
    }

    public ChatroomUserCommandHandler(ChatroomUsersWrite chatroomUsersWrite) {
        this.chatroomUsersWrite = chatroomUsersWrite;
    }

    @Override
    public Integer handleCommand(ChatroomUserCommand command) {
        switch (command) {
            case ChatroomUserCommand.AddUsers(
                    ChatroomUsersSaveData saveData) -> {

                return chatroomUsersWrite.saveAll(fillChatroomUsers(saveData)).size();

            }
            case ChatroomUserCommand.ChangeMembershipStatus(
                    int joinId,
                    MembershipStatus status) -> {

                return chatroomUsersWrite.changeMembership(joinId, status);

            }
            case ChatroomUserCommand.ChangeUserRole(
                    int joinId,
                    ChatroomRole role) -> {

                return chatroomUsersWrite.changeRole(joinId, role);

            }

        }
    }

    private static List<ChatroomUser> fillChatroomUsers(ChatroomUsersSaveData saveData) {
        List<ChatroomUser> entities = new ArrayList<>(
                saveData.userToRole().size());

        for (Map.Entry<Integer, ChatroomRole> entry
                : saveData.userToRole().entrySet()) {
            ChatroomUser cu = new ChatroomUser();
            cu.setChatroomId(saveData.chatroomId());
            cu.setUserId(entry.getKey());
            cu.setRole(entry.getValue());
            entities.add(cu);
        }

        return entities;
    }
}