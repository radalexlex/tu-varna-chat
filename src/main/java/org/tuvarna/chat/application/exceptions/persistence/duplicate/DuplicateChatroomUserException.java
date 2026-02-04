package org.tuvarna.chat.application.exceptions.persistence.duplicate;

import jakarta.data.exceptions.EntityExistsException;

public class DuplicateChatroomUserException extends EntityExistsException {
    public DuplicateChatroomUserException(String message) {
        super(message);
    }
}
