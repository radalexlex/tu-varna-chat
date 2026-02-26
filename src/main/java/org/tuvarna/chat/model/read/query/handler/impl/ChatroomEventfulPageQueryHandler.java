package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.query.ChatroomEventfulPagedQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.repository.projection.ChatroomEventfulRead;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
@Named("ChatroomEventfulPageQueryHandler")
public class ChatroomEventfulPageQueryHandler implements QueryHandler<List<ChatroomEventfulElement>, ChatroomEventfulPagedQuery> {

    ChatroomEventfulRead repository;

    @Inject
    public ChatroomEventfulPageQueryHandler(ChatroomEventfulRead repository) {
        this.repository = repository;
    }

    @Override
    public List<ChatroomEventfulElement> handleQuery(ChatroomEventfulPagedQuery query) {
        switch (query) {
            case ChatroomEventfulPagedQuery.GetFirstPageEventOrdered(long userId) -> {

                List<ChatroomEventfulElement> elements = repository.findPageChatroomEventful(
                        userId,
                        null,
                        0,
                        0L
                );

                return elements;

            }

            case ChatroomEventfulPagedQuery.GetFollowingPageEventOrdered(
                    long userId,
                    Instant latestEventTimeOnPage,
                    Integer latestChatroomIdOnPage,
                    Long latestChatMessageIdOnPage) -> {

                List<ChatroomEventfulElement> elements = repository.findPageChatroomEventful(
                        userId,
                        latestEventTimeOnPage,
                        latestChatroomIdOnPage,
                        latestChatMessageIdOnPage
                );

                return elements;

            }
        }
    }
}
