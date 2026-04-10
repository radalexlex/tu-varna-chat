package org.tuvarna.chat.application.exceptions.service;

public class ChatroomServiceException extends ServiceException {

    public ChatroomServiceException(String message) {
        super(message);
    }

    public ChatroomServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatroomServiceException(Throwable cause) {
        super(cause);
    }

}