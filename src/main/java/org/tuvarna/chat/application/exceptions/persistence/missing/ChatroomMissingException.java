package org.tuvarna.chat.application.exceptions.persistence.missing;

import jakarta.data.exceptions.EmptyResultException;

public class ChatroomMissingException extends EmptyResultException {
    public ChatroomMissingException(String message) {
        super(message);
    }
}
