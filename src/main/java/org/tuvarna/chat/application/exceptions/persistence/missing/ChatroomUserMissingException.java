package org.tuvarna.chat.application.exceptions.persistence.missing;

import java.util.NoSuchElementException;

public class ChatroomUserMissingException extends NoSuchElementException {
    public ChatroomUserMissingException(String message) {
        super(message);
    }
}
