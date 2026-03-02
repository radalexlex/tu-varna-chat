package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.application.exceptions.notfound.ChatroomUserNotFoundException;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.query.DetailQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.repository.domain.ChatroomUsersRead;

@ApplicationScoped
@Named("ChatroomUserQueryHandler")
public class ChatroomUserQueryDetailHandler implements QueryHandler<ChatroomUserDetails, DetailQuery<Long>> {

    ChatroomUsersRead repository;

    @Inject
    public ChatroomUserQueryDetailHandler(ChatroomUsersRead repository) {
        this.repository = repository;
    }

    @Override
    public ChatroomUserDetails handleQuery(DetailQuery<Long> query) {

        switch (query) {
            case DetailQuery.GetData<Long>(Long userId) -> {
                ChatroomUser cu = repository
                        .getUserByUserId(userId)
                        .orElseThrow(() -> new ChatroomUserNotFoundException(
                                "User with id={" + userId + "} not found"));

                return new ChatroomUserDetails(
                        cu.getId(),
                        cu.getChatroomId(),
                        cu.getUserId(),
                        cu.getRole().toString(),
                        cu.getStatus().toString(),
                        cu.getJoinTime().toString());
            }
        }
    }
}
