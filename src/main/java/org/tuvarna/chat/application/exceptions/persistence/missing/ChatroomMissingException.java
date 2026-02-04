package org.tuvarna.chat.application.exceptions.persistence.missing;

import java.util.NoSuchElementException;

public class ChatroomMissingException extends NoSuchElementException {
    public ChatroomMissingException(String message) {
        super(message);
    }
}
