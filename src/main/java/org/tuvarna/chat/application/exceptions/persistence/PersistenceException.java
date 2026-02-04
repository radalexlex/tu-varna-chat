package org.tuvarna.chat.application.exceptions.persistence;

public class PersistenceException extends RuntimeException {
    public PersistenceException(String message) {
        super(message);
    }
}
