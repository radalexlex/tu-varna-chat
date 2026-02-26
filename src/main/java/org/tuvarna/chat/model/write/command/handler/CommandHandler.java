package org.tuvarna.chat.model.write.command.handler;

public interface CommandHandler<T, C> {
    T handleCommand(C command);
}
