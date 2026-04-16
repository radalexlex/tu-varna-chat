package org.tuvarna.chat.application.exceptions.persistence.missing;

import org.tuvarna.chat.application.exceptions.persistence.DataPersistenceException;

public class ChatroomMissingException extends DataPersistenceException {
    public ChatroomMissingException(String message) {
        super(message);
    }
}
