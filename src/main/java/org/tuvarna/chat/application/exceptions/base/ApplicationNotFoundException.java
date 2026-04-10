package org.tuvarna.chat.application.exceptions.base;

import jakarta.data.exceptions.EmptyResultException;

public class ApplicationNotFoundException extends EmptyResultException {
    public ApplicationNotFoundException(String message) {
        super(message);
    }

}
