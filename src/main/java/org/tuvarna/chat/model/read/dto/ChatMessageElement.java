package org.tuvarna.chat.model.read.dto;

import java.time.Instant;

public record ChatMessageElement(long id,
                                 String uuid,
                                 long senderUser,
                                 String timeSent,
                                 String content) {
}
