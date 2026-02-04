package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.query.ChatroomEventfulPagedQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.repository.projection.ChatroomEventfulRead;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
@Named("ChatroomEventfulQueryHandler")
public class ChatroomEventfulQueryHandler implements QueryHandler<List<ChatroomEventfulElement>, ChatroomEventfulPagedQuery> {

    ChatroomEventfulRead repository;

    @Override
    public List<ChatroomEventfulElement> handleQuery(ChatroomEventfulPagedQuery query) {
        switch (query) { // page of dtos
            case ChatroomEventfulPagedQuery.GetFirstPageEventOrdered (int chatroomUserId) -> {

                List<ChatroomEventfulElement> elements = repository.findPageChatroomEventful(
                        chatroomUserId,
                        null,
                        0,
                        0
                );

                return elements;

            }

            case ChatroomEventfulPagedQuery.GetFollowingPageEventOrdered (int chatroomUserId,
                                                                          Instant latestEventTimeOnPage,
                                                                          Integer latestChatroomIdOnPage,
                                                                          Integer latestChatMessageIdOnPage) -> {

                List<ChatroomEventfulElement> elements = repository.findPageChatroomEventful(
                        chatroomUserId,
                        latestEventTimeOnPage,
                        latestChatroomIdOnPage,
                        latestChatMessageIdOnPage
                );

                return elements;

            }
        }
    }
}
