package org.tuvarna.chat.application.exceptions.conflict.room;

import org.tuvarna.chat.application.exceptions.base.ApplicationConflictException;

public class ChatroomAlreadyDeletedException extends ApplicationConflictException {
    public ChatroomAlreadyDeletedException(String message) {
        super(message);
    }
}
