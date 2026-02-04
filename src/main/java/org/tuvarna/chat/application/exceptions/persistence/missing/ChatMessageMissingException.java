package org.tuvarna.chat.application.exceptions.persistence.missing;

import java.util.NoSuchElementException;

public class ChatMessageMissingException extends NoSuchElementException {
    public ChatMessageMissingException(String message) {
        super(message);
    }
}
