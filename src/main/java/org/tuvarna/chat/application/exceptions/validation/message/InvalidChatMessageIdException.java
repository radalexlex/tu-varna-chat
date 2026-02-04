package org.tuvarna.chat.application.exceptions.validation.message;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;

public class InvalidChatMessageIdException extends ApplicationValidationException {
    public InvalidChatMessageIdException(String message) {
        super(message);
    }
}
