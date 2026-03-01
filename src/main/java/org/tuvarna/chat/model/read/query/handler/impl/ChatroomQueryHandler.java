package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.application.exceptions.notfound.ChatroomNotFoundException;
import org.tuvarna.chat.model.entity.postgres.Chatroom;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;
import org.tuvarna.chat.model.read.query.DetailQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.repository.domain.ChatroomsRead;

@ApplicationScoped
@Named("ChatroomQueryHandler")
public class ChatroomQueryHandler implements QueryHandler<ChatroomOverview, DetailQuery<Integer>> {

    ChatroomsRead repository;

    @Inject
    public ChatroomQueryHandler(ChatroomsRead repository) {
        this.repository = repository;
    }

    @Override
    public ChatroomOverview handleQuery(DetailQuery<Integer> query) {
        switch (query) {
            case DetailQuery.GetData(Integer chatroomId) -> {

                Chatroom c = repository.findById(chatroomId)
                        .orElseThrow(() -> new ChatroomNotFoundException(
                                "Chatroom with chatroomId = " +
                                        "{" + chatroomId + "} not found"));

                return new ChatroomOverview(
                        chatroomId,
                        c.getName(),
                        c.getCreatedAt().toString());
            }
        }
    }
}
