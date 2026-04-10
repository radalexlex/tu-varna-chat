package org.tuvarna.chat.model.read.dto;

public record ChatMessageElement(long id,
                                 String uuid,
                                 long senderUser,
                                 String timeSent,
                                 String content) {
}
