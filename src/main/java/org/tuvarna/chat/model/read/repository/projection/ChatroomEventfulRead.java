package org.tuvarna.chat.model.read.repository.projection;

import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;

import java.time.Instant;
import java.util.List;

public interface ChatroomEventfulRead {

    List<ChatroomEventfulElement> findPageChatroomEventful(
            int userId,
            Instant lastActivity,
            Integer lastChatroomId,
            Integer lastChatMessageId
    );

}



