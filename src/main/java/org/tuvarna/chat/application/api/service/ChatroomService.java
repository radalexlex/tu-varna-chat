package org.tuvarna.chat.application.api.service;

import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.time.Instant;
import java.util.List;

public interface ChatroomService {
    int createChatroom(long adminId, String name);

    int archiveChatroom(long userId, int chatroomId);

    // I assume that the chatroomUserId passed is a valid chatroomUserId securely passed from the upper services
    ContentPage<ChatroomEventfulElement> getChatroomEventfulElements(long userId,
                                                                     Instant latestEventTimeOnPage,
                                                                     Integer latestChatroomIdOnPage,
                                                                     Long latestChatMessageIdOnPage);

    ChatroomOverview getChatroomOverview(long requestingUserId, int chatroomId);

    List<Integer> getChatroomIdsForUser(long requestingUserId);
}
