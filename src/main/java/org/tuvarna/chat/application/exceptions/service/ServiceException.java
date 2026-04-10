package org.tuvarna.chat.application.exceptions.service;

import org.tuvarna.chat.application.exceptions.base.ApplicationException;

public class ServiceException extends ApplicationException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }
}
