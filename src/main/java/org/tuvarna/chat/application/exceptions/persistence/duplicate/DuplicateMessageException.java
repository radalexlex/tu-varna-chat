package org.tuvarna.chat.application.exceptions.persistence.duplicate;

import jakarta.data.exceptions.EntityExistsException;

public class DuplicateMessageException extends EntityExistsException {
    public DuplicateMessageException(String message) {
        super(message);
    }
}
