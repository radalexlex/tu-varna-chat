package org.tuvarna.chat.model.read.dto;

public record ChatroomEventfulElement(
        Long userId,
        int chatroomId,
        Long lastRead,
        String lastReadTimestamp,
        String displayName,
        boolean isPrivateChat,
        String URLImage,
        String messageContent,
        Long lastChatMessageId,
        String clientMessageId,
        String activityTime
) {
}

