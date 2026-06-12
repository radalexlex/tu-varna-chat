package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.entity.postgres._ChatMessage;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatMessagePageData;
import org.tuvarna.chat.model.read.repository.domain.ChatMessagesRead;

import javax.naming.ldap.PagedResultsControl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("ChatMessagePageQueryHandler")
public class ChatMessagePageQueryHandler implements QueryHandler<ContentPage<ChatMessageElement>, PageQuery<Integer, ChatMessagePageData>> {

    private static final int PAGE_SIZE = 25;

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
                                m.getSenderUser().getUserId(),
                                m.getTimeSent().toString(),
                                m.getContent());
                    } else {
                        return new ChatMessageElement(
                                m.getId(),
                                m.getClientMessageId().toString(),
                                m.getSenderUserId(),
                                m.getSenderUser().getUserId(),
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

                PageRequest up;
                PageRequest down;
                CursoredPage<ChatMessage> pgUp;
                CursoredPage<ChatMessage> pgDown;
                List<ChatMessage> totalElements;
                boolean hasNextPage;
                boolean hasPreviousPage;

                if(data == null) { // fallback, offset
                    PageRequest fallbackRequest = PageRequest.ofSize(PAGE_SIZE);
                    Page<ChatMessage> fallbackLastPage = repository.findLastPage(chatroomId, fallbackRequest);

                    Collections.reverse(fallbackLastPage.content());
                    totalElements = fallbackLastPage.content();

                    hasNextPage = fallbackLastPage.hasPrevious();
                    hasPreviousPage = fallbackLastPage.hasNext();
                } else {
                    if(data.initialRequest()) {
                        up = PageRequest.ofSize(PAGE_SIZE)
                                .beforeCursor(PageRequest.Cursor.forKey(
                                        data.messageCursorId()
                                ));
                        down = PageRequest.ofSize(PAGE_SIZE)
                                .afterCursor(PageRequest.Cursor.forKey(
                                        data.messageCursorId()
                                ));
                        ChatMessage center = repository.findById(data.messageCursorId()).orElse(null);

                        pgUp = repository.findByChatroomIdCursoredAsc(chatroomId, up);
                        pgDown = repository.findByChatroomIdCursoredAsc(chatroomId, down);

                        totalElements = new ArrayList<>(pgUp.content());
                        if(center != null) {
                            totalElements.add(center);
                        }
                        totalElements.addAll(pgDown.content());

                        hasNextPage = !totalElements.isEmpty();
                        hasPreviousPage = true;

                    } else {
                        if(data.downScroll()) {
                            down = PageRequest.ofSize(PAGE_SIZE)
                                    .afterCursor(PageRequest.Cursor.forKey(
                                            data.messageCursorId()
                                    ));
                            pgDown = repository.findByChatroomIdCursoredAsc(chatroomId, down);

                            totalElements = pgDown.content();

                            hasNextPage = !totalElements.isEmpty();
                            hasPreviousPage = true;

                        } else {
                            up = PageRequest.ofSize(PAGE_SIZE)
                                    .beforeCursor(PageRequest.Cursor.forKey(
                                            data.messageCursorId()
                                    ));
                            pgUp = repository.findByChatroomIdCursoredAsc(chatroomId, up);

                            totalElements = pgUp.content();

                            hasNextPage = true;
                            hasPreviousPage = !totalElements.isEmpty();
                        }
                    }
                }
                return new ContentPage<ChatMessageElement>(
                        toElements(totalElements),
                        hasNextPage,
                        hasPreviousPage
                );
            }
        }
    }


    //CursoredPage<ChatMessage> midPage = null;
    //                Page<ChatMessage> firstPage = null;
    //                PageRequest pageRequest;
    //                List<ChatMessage> firstPageContentReversed = null;
    //                if (data == null) {
    //                    pageRequest = PageRequest.ofSize(PAGE_SIZE).withTotal();
    //
    //                    firstPage = repository.findLastPage(chatroomId, pageRequest);
    //
    //                    List<ChatMessage> content = new ArrayList<>(firstPage.content());
    //
    //                    return new ContentPage<>(
    //                            toElements(content),
    //                            false,
    //                            firstPage.hasNext()
    //                    );
    //
    //                } else {
    //                    if(data.initialRequest()) {
    //
    //                        pageRequest = PageRequest.ofSize(PAGE_SIZE)
    //                                .beforeCursor(PageRequest.Cursor.forKey(
    //                                        data.lastTimestamp(),
    //                                        data.lastId()
    //                                ));
    //                        CursoredPage <ChatMessage> pgUp = repository.findByChatroomIdCursoredAsc(chatroomId, pageRequest);
    //
    //                        PageRequest pageRequestDown =  PageRequest.ofSize(PAGE_SIZE)
    //                                .afterCursor(PageRequest.Cursor.forKey(
    //                                        data.lastTimestamp(),
    //                                        data.lastId() - 2
    //                                ));
    //                        CursoredPage <ChatMessage> pgDn = repository.findByChatroomIdCursoredAsc(chatroomId, pageRequestDown);
    //
    //                        List<ChatMessage> totalElements = new ArrayList<>(pgUp.content());
    //                        totalElements.addAll(pgDn.content());
    //
    //                        return new ContentPage<>(
    //                            toElements(totalElements),
    //                                pgDn.hasNext(),
    //                                pgUp.hasPrevious()
    //                        );
    //
    //
    //                    } else if (data.requestForNext()) { // scroll down
    //                        pageRequest = PageRequest.ofSize(PAGE_SIZE)
    //                                .afterCursor(PageRequest.Cursor.forKey(
    //                                        data.lastTimestamp(),
    //                                        data.lastId()));
    //                        midPage = repository.findByChatroomIdCursoredAsc(chatroomId, pageRequest);
    //                        return new ContentPage<>(
    //                                toElements(midPage.content()),
    //                                midPage.hasNext(),
    //                                midPage.hasPrevious()
    //                        );
    //                    } else { // scroll up
    //                        pageRequest = PageRequest.ofSize(PAGE_SIZE)
    //                                .afterCursor(PageRequest.Cursor.forKey(
    //                                        data.firstTimestamp(),
    //                                        data.firstId()));
    //                        midPage = repository.findByChatroomIdCursoredDesc(chatroomId, pageRequest);
    //                        return new ContentPage<>(
    //                                toElements(midPage.content()),
    //                                midPage.hasPrevious(),
    //                                midPage.hasNext()
    //                        );
    //                    }
    //
    //
    //                }


//                    List<ChatMessage> content = new ArrayList<>(midPage.content());
//                    content.sort(Comparator
//                            .comparing(ChatMessage::getTimeSent)
//                            .thenComparing(ChatMessage::getId));


}
