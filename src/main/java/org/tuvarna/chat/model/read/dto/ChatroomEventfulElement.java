package org.tuvarna.chat.model.read.dto;

public record ChatroomEventfulElement(
        long userId,
        int chatroomId,
        String chatroomName,
        String messageContent,
        long lastChatMessageId,
        String clientMessageId,
        String activityTime
) {
}

