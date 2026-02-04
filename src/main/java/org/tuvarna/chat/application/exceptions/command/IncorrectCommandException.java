package org.tuvarna.chat.application.exceptions.command;

import org.tuvarna.chat.application.exceptions.base.ApplicationCommandException;

public class IncorrectCommandException extends ApplicationCommandException {
    public IncorrectCommandException(String message) {
        super(message);
    }
}
