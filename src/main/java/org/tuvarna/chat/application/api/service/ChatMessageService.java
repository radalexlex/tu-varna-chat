package org.tuvarna.chat.application.api.service;

import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.write.dto.ChatMessageOperationalData;

import java.time.Instant;
import java.util.List;

public interface ChatMessageService {
    int addMessages(List<ChatMessageOperationalData> saveData);

    int archiveMessage(long requestingUserId, ChatMessageElement message, int chatroomId);

    int updateMessage(long requestingUserId, ChatMessageElement message, int chatroomId, String newContent);

    ContentPage<ChatMessageElement> getMessagePage(long requestingUserId, int chatroomId, Instant oldestTimestamp, Integer oldestId );
}
