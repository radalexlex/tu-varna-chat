package org.tuvarna.chat.application.exceptions.validation.user;

import org.tuvarna.chat.application.exceptions.base.ApplicationValidationException;

public class InvalidUserMembershipStatusException extends ApplicationValidationException {
    public InvalidUserMembershipStatusException(String message) {
        super(message);
    }
}
