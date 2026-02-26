package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.ChatMessagePagedQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.repository.domain.ChatMessagesRead;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("ChatMessagePageQueryHandler")
public class ChatMessagePageQueryHandler implements QueryHandler<ContentPage<ChatMessageElement>, ChatMessagePagedQuery> {

    private static final int PAGE_SIZE = 50;

    ChatMessagesRead repository;

    @Inject
    public ChatMessagePageQueryHandler(ChatMessagesRead repository) {
        this.repository = repository;
    }

    private static List<ChatMessageElement> toElements(
            List<ChatMessage> entityInput) {

        return entityInput.stream()
                .map(m -> new ChatMessageElement(
                        m.getId(),
                        m.getClientMessageId().toString(),
                        m.getSenderUserId(),
                        m.getTimeSent().toString(),
                        m.getContent()))
                .collect(Collectors.toList());
    }

    @Override // paged dto query
    public ContentPage<ChatMessageElement> handleQuery(ChatMessagePagedQuery query) {
        switch (query) {
            case ChatMessagePagedQuery.GetFirstPage(int chatroomId) -> {

                PageRequest pageRequest = PageRequest.ofSize(PAGE_SIZE);

                CursoredPage<ChatMessage> p =
                        repository.findByChatroomIdAndDeleted(chatroomId, false, pageRequest);

                return new ContentPage<ChatMessageElement>(
                        toElements(p.content()),
                        p.hasPrevious());
            }

            case ChatMessagePagedQuery.GetFollowingPage(
                    int chatroomId,
                    Instant oldestTimestamp,
                    int oldestId
            ) -> {

                PageRequest pageRequest = PageRequest.ofSize(PAGE_SIZE)
                        .beforeCursor(PageRequest
                                .Cursor.forKey(
                                        oldestTimestamp,
                                        oldestId));

                CursoredPage<ChatMessage> p =
                        repository.findByChatroomIdAndDeleted(chatroomId, false, pageRequest);

                return new ContentPage<ChatMessageElement>(
                        toElements(p.content()),
                        p.hasPrevious());
            }

        }
    }
}
