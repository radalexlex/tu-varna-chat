package org.tuvarna.chat.application.exceptions.violation.user;

import org.tuvarna.chat.application.exceptions.base.ApplicationRuleViolationException;

public class UserNotChatroomMemberException extends ApplicationRuleViolationException {
    public UserNotChatroomMemberException(String message) {
        super(message);
    }
}
