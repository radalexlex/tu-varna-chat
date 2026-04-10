package org.tuvarna.chat.model.write.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ChatMessageOperationalData(
        String clientMessageId,
        int chatroomId,
        Long senderId,
        Long senderUserId,
        String content,
        String creationTimestamp) {
}
