package org.tuvarna.chat.application.exceptions.validation.message;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;

public class MessageTooLongException extends ApplicationValidationException {
    public MessageTooLongException(String message) {
        super(message);
    }
}
