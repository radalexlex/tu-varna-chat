package org.tuvarna.chat.application.exceptions.service;

public class ChatMessageServiceException extends ServiceException {

    public ChatMessageServiceException(String message) {
        super(message);
    }

    public ChatMessageServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatMessageServiceException(Throwable cause) {
        super(cause);
    }
}
