package org.tuvarna.chat.model.read.repository.projection;

import java.time.Instant;
import java.util.UUID;

public record ChatroomEventfulUnprocessed(
        Long userId,
        Long lastRead,
        Instant lastReadTimestamp,
        int chatroomId,
        String displayName,
        boolean isPrivateChat,
        String messageContent,
        UUID clientMessageId,
        Long chatMessageId,
        Instant activityTime
) {}


