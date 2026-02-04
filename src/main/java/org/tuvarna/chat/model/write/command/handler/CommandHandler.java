package org.tuvarna.chat.model.write.command.handler;

import java.util.Optional;

public interface CommandHandler<K, T> {
    K handleCommand(T command);
}
