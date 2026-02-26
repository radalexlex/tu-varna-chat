package org.tuvarna.chat.model.read.dto;

import java.time.Instant;
import java.util.UUID;

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

