package org.tuvarna.chat.model.write.dto;

import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;

import java.util.Map;

public record ChatroomUsersSaveData(
        int chatroomId,
        Map<Integer, ChatroomRole> userToRole) {
}
