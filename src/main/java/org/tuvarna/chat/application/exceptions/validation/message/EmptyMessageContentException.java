package org.tuvarna.chat.application.exceptions.validation.message;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;

public class EmptyMessageContentException extends ApplicationValidationException {

    public EmptyMessageContentException(String message) {
        super(message);
    }

}
