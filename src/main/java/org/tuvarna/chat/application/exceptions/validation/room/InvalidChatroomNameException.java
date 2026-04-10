package org.tuvarna.chat.application.exceptions.validation.room;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;

public class InvalidChatroomNameException extends ApplicationValidationException {
    public InvalidChatroomNameException(String message) {
        super(message);
    }
}
