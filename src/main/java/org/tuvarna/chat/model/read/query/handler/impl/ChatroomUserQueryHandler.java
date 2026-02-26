package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.application.exceptions.notfound.ChatroomUserNotFoundException;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.query.ChatroomUserQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.repository.domain.ChatroomUsersRead;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Named("ChatroomUserQueryHandler")
public class ChatroomUserQueryHandler implements QueryHandler<ChatroomUserDetails, ChatroomUserQuery> {

    ChatroomUsersRead repository;

    @Inject
    public ChatroomUserQueryHandler(ChatroomUsersRead repository) {
        this.repository = repository;
    }

    @Override
    public ChatroomUserDetails handleQuery(ChatroomUserQuery query) {

        switch (query) {
            case ChatroomUserQuery.GetChatroomUserDetails(long userId) -> {
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
