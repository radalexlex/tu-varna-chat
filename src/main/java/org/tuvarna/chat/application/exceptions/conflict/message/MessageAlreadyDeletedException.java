package org.tuvarna.chat.application.exceptions.conflict.message;

import org.tuvarna.chat.application.exceptions.base.ApplicationConflictException;

public class MessageAlreadyDeletedException extends ApplicationConflictException {
    public MessageAlreadyDeletedException(String message) {
        super(message);
    }
}
