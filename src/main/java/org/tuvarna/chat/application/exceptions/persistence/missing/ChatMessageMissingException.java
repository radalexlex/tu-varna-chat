package org.tuvarna.chat.application.exceptions.persistence.missing;

import org.tuvarna.chat.application.exceptions.persistence.DataPersistenceException;

public class ChatMessageMissingException extends DataPersistenceException {
    public ChatMessageMissingException(String cause) {
        super(cause);
    }
}
