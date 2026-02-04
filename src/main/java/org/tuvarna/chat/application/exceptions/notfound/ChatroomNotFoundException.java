package org.tuvarna.chat.application.exceptions.notfound;

import org.tuvarna.chat.application.exceptions.base.ApplicationNotFoundException;

public class ChatroomNotFoundException extends ApplicationNotFoundException {
    public ChatroomNotFoundException(String message) {
        super(message);
    }
}
