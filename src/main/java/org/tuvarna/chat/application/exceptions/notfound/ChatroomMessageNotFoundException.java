package org.tuvarna.chat.application.exceptions.notfound;

import org.tuvarna.chat.application.exceptions.base.ApplicationNotFoundException;

public class ChatroomMessageNotFoundException extends ApplicationNotFoundException {
    public ChatroomMessageNotFoundException(String message) {
        super(message);
    }
}
