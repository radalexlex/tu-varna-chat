package org.tuvarna.chat.application.exceptions.notfound;

import org.tuvarna.chat.application.exceptions.base.ApplicationNotFoundException;

public class ChatroomUserNotFoundException extends ApplicationNotFoundException {
    public ChatroomUserNotFoundException(String message) {
        super(message);
    }
}
