package org.tuvarna.chat.model.write.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ChatMessageOperationalData (
        String clientMessageId,
        int chatroomId,
        long senderId,
        String content,
        String creationTimestamp) {
}
