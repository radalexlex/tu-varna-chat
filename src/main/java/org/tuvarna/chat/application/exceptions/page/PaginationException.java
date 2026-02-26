package org.tuvarna.chat.application.exceptions.page;

import org.tuvarna.chat.application.exceptions.base.ApplicationException;

public class PaginationException extends ApplicationException {
    public PaginationException(String message) {
        super(message);
    }
}
