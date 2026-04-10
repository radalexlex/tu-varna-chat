package org.tuvarna.chat.application.exceptions.persistence;

import org.tuvarna.chat.application.exceptions.base.ApplicationException;

public class DataPersistenceException extends ApplicationException {
    public DataPersistenceException(String message) {
        super(message);
    }
}
