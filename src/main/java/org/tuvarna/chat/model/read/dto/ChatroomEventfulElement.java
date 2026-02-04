package org.tuvarna.chat.model.read.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatroomEventfulElement(
        int userId,
        int chatroomId,
        String chatroomName,
        String messageContent,
        long lastChatMessageId,
        UUID clientMessageId,
        Instant activityTime
) {}

