package org.tuvarna.chat.application.exceptions.violation.room;

import org.tuvarna.chat.application.exceptions.base.ApplicationRuleViolationException;

public class ChatroomArchivedException extends ApplicationRuleViolationException {
    public ChatroomArchivedException(String message) {
        super(message);
    }
}
