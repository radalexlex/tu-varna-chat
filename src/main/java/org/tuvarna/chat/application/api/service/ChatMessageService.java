package org.tuvarna.chat.application.api.service;

import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;
import org.tuvarna.chat.model.write.dto.enums.AckStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ChatMessageService {
    Map<AckStatus, List<ChatMessageOperationalData>> addMessages(List<ChatMessageOperationalData> saveData);

    int archiveMessage(long requestingUserId, ChatMessageElement message, int chatroomId);

    int updateMessage(long requestingUserId, ChatMessageElement message, int chatroomId, String newContent);

    ContentPage<ChatMessageElement> getMessagePage(long requestingUserId,
                                                   int chatroomId,
                                                   Long messageCursorId,
                                                   boolean downScroll,
                                                   boolean initialRequest);
}
