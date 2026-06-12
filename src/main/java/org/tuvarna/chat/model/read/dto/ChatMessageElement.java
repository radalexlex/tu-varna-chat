package org.tuvarna.chat.model.read.dto;

public record ChatMessageElement(long id,
                                 String uuid,
                                 long senderId,
                                 long senderUserId,
                                 String timeSent,
                                 String content) {
}
