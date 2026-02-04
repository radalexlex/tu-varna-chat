package org.tuvarna.chat.application.exceptions.persistence;

public class MessagePersistenceException extends PersistenceException {
    public MessagePersistenceException(String message) {
        super(message);
    }
}
