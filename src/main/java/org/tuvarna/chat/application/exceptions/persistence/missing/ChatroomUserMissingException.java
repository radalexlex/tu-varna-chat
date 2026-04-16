package org.tuvarna.chat.application.exceptions.persistence.missing;

import org.tuvarna.chat.application.exceptions.persistence.DataPersistenceException;

public class ChatroomUserMissingException extends DataPersistenceException {
    public ChatroomUserMissingException(String message) {
        super(message);
    }
}
