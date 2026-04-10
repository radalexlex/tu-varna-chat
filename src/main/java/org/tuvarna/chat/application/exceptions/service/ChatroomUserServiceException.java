package org.tuvarna.chat.application.exceptions.service;

public class ChatroomUserServiceException extends ServiceException {

    public ChatroomUserServiceException(String message) {
        super(message);
    }

    public ChatroomUserServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatroomUserServiceException(Throwable cause) {
        super(cause);
    }

}
