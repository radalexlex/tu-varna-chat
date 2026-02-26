package org.tuvarna.chat.application.exceptions.violation.user;

import org.tuvarna.chat.application.exceptions.base.ApplicationRuleViolationException;

public class UserNotAllowedException extends ApplicationRuleViolationException {
    public UserNotAllowedException(String message) {
        super(message);
    }
}
