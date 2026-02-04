package org.tuvarna.chat.application.exceptions.validation.user;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;

public class InvalidUserRoleException extends ApplicationValidationException {
    public InvalidUserRoleException(String message) {
        super(message);
    }
}
