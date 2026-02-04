package org.tuvarna.chat.application.exceptions.validation.room;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;

public class InvalidChatroomIdException extends ApplicationValidationException {
    public InvalidChatroomIdException(String message) {
        super(message);
    }
}
