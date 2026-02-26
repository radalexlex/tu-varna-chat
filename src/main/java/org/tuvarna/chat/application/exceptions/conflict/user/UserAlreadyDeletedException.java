package org.tuvarna.chat.application.exceptions.conflict.user;

public class UserAlreadyDeletedException extends RuntimeException {
    public UserAlreadyDeletedException(String message) {
        super(message);
    }
}
