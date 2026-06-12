package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatroomEventfulPageData;
import org.tuvarna.chat.model.read.repository.projection.ChatroomEventfulRead;
import org.tuvarna.chat.model.read.repository.projection.ChatroomEventfulUnprocessed;

@ApplicationScoped
@Named("ChatroomEventfulPageQueryHandler")
public class ChatroomEventfulPageQueryHandler implements QueryHandler<ContentPage<ChatroomEventfulUnprocessed>, PageQuery<Long, ChatroomEventfulPageData>> {

    ChatroomEventfulRead repository;

    @Inject
    public ChatroomEventfulPageQueryHandler(ChatroomEventfulRead repository) {
        this.repository = repository;
    }

    @Override
    public ContentPage<ChatroomEventfulUnprocessed> handleQuery(PageQuery<Long, ChatroomEventfulPageData> query) {
        switch (query) {
            case PageQuery.GetPage(Long userId, ChatroomEventfulPageData data) -> {
                if (data == null) {
                    return repository.findPageChatroomEventful(
                            userId, null, 0, 0L);
                } else {
                    return repository.findPageChatroomEventful(
                            userId,
                            data.latestEventTimeOnPage(),
                            data.latestChatroomIdOnPage(),
                            data.latestChatMessageIdOnPage()
                    );
                }
            }
        }
    }


}