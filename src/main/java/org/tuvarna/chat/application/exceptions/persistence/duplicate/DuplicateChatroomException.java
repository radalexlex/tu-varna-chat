package org.tuvarna.chat.application.exceptions.persistence.duplicate;

import jakarta.data.exceptions.EntityExistsException;

public class DuplicateChatroomException extends EntityExistsException {
    public DuplicateChatroomException(String message) {
        super(message);
    }
}
