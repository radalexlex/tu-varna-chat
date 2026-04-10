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
import org.tuvarna.chat.utils.Pair;

@ApplicationScoped
@Named("ChatroomUserQueryDetailHandler")
public class ChatroomUserQueryDetailHandler implements QueryHandler<ChatroomUserDetails, DetailQuery<Pair<Long, Integer>>> {

    ChatroomUsersRead repository;

    @Inject
    public ChatroomUserQueryDetailHandler(ChatroomUsersRead repository) {
        this.repository = repository;
    }

    @Override
    public ChatroomUserDetails handleQuery(DetailQuery<Pair<Long, Integer>> query) {

        switch (query) {
            case DetailQuery.GetData<Pair<Long, Integer>>(
                    Pair<Long, Integer> userToChatroom
            ) -> {
                ChatroomUser cu = repository
                        .getUserByUserIdAndChatroomId(userToChatroom.a(), userToChatroom.b())
                        .orElseThrow(() -> new ChatroomUserNotFoundException(
                                "User with id={" + userToChatroom.a()
                                        + "} not found in chatroom "
                                        + userToChatroom.b()));

                return new ChatroomUserDetails(
                        cu.getId(),
                        cu.getChatroomId(),
                        cu.getUserId(),
                        cu.getRole().toString(),
                        cu.getStatus().toString(),
                        cu.getJoinTime().toString(),
                        cu.getLastRead());
            }
        }
    }
}
