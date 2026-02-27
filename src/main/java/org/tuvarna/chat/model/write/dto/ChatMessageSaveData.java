package org.tuvarna.chat.model.write.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.Instant;

@RegisterForReflection
public record ChatMessageSaveData(
        String clientMessageId,
        int chatroomId,
        long senderId,
        String content,
        String creationTimestamp) {
}
