package org.tuvarna.chat.model.write.dto;

import java.time.Instant;

public record ChatMessageSaveData(
        String clientMessageId,
        int chatroomId,
        int senderId,
        String content,
        Instant creationTimestamp) {
}
