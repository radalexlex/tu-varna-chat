package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatMessagePageData;
import org.tuvarna.chat.model.read.repository.domain.ChatMessagesRead;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("ChatMessagePageQueryHandler")
public class ChatMessagePageQueryHandler implements QueryHandler<ContentPage<ChatMessageElement>, PageQuery<Integer, ChatMessagePageData>> {

    private static final int PAGE_SIZE = 50;

    ChatMessagesRead repository;

    @Inject
    public ChatMessagePageQueryHandler(ChatMessagesRead repository) {
        this.repository = repository;
    }

    private static List<ChatMessageElement> toElements(
            List<ChatMessage> entityInput) {

        return entityInput.stream()
                .map((m) -> {
                    if (!m.isDeleted()) {
                        return new ChatMessageElement(
                                m.getId(),
                                m.getClientMessageId().toString(),
                                m.getSenderUserId(),
                                m.getTimeSent().toString(),
                                m.getContent());
                    } else {
                        return new ChatMessageElement(
                                m.getId(),
                                m.getClientMessageId().toString(),
                                m.getSenderUserId(),
                                m.getTimeSent().toString(),
                                null);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public ContentPage<ChatMessageElement> handleQuery(PageQuery<Integer, ChatMessagePageData> query) {

        switch (query) {
            case PageQuery.GetPage<Integer, ChatMessagePageData>
                         (Integer chatroomId, ChatMessagePageData data) -> {

                CursoredPage<ChatMessage> p;
                PageRequest pageRequest;

                if (data == null) {
                    pageRequest = PageRequest.ofSize(PAGE_SIZE);
                    p = repository.findByChatroomIdOlder(chatroomId, pageRequest);
                } else {
                    pageRequest = PageRequest.ofSize(PAGE_SIZE)
                            .beforeCursor(PageRequest
                                    .Cursor.forKey(
                                            data.lastTimestamp(),
                                            data.lastId()));
                    if (data.requestForOlder()) {
                        p = repository.findByChatroomIdOlder(chatroomId, pageRequest);
                    } else {
                        p = repository.findByChatroomIdNewer(chatroomId, pageRequest);
                    }
                }

                if (p != null) {
                    return new ContentPage<ChatMessageElement>(
                            toElements(p.content()),
                            p.hasPrevious());
                } else {
                    throw new PaginationException("Can't fetch content page");
                }
            }
        }
    }


}
