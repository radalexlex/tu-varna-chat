package org.tuvarna.chat.model.read.query.handler.impl;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.ChatroomUserPagedQuery;
import org.tuvarna.chat.model.read.query.handler.QueryHandler;
import org.tuvarna.chat.model.read.repository.domain.ChatroomUsersRead;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Named("ChatroomUserPageQueryHandler")
public class ChatroomUserPageQueryHandler implements QueryHandler<ContentPage<ChatroomUserDetails>, ChatroomUserPagedQuery> {

    private static final int PAGE_SIZE = 25;

    ChatroomUsersRead repository;

    @Inject
    public ChatroomUserPageQueryHandler(ChatroomUsersRead repository) {
        this.repository = repository;
    }

    private static List<ChatroomUserDetails> toDetails(
            Stream<ChatroomUser> inputStream) {

        return inputStream.map(d -> new ChatroomUserDetails(
                        d.getId(),
                        d.getChatroomId(),
                        d.getUserId(),
                        d.getRole().toString(),
                        d.getStatus().toString(),
                        d.getJoinTime().toString()))
                .collect(Collectors.toList());
    }

    private static List<ChatroomUserDetails> toLimitedDetails(
            Stream<ChatroomUser> inputStream) {
        return toDetails(inputStream
                .filter(d
                        -> d.getStatus() == MembershipStatus.ACTIVE));
    }

    @Override
    public ContentPage<ChatroomUserDetails> handleQuery(ChatroomUserPagedQuery query) {

        CursoredPage<ChatroomUser> p;
        boolean exPermission = false;

        switch (query) {

            case ChatroomUserPagedQuery.GetFirstPage(
                    int chatroomId,
                    boolean extendedPermissionGiven
            ) -> {

                exPermission = extendedPermissionGiven;

                PageRequest pageRequest = PageRequest.ofSize(PAGE_SIZE);

                p = repository.findChatroomUsersPage(chatroomId, pageRequest);

            }

            case ChatroomUserPagedQuery.GetFollowingPage(
                    int chatroomId,
                    Instant oldestAdditionTimestamp,
                    int oldestAdditionId,
                    boolean extendedPermissionGiven
            ) -> {

                exPermission = extendedPermissionGiven;

                PageRequest pageRequest =
                        PageRequest.ofSize(PAGE_SIZE)
                                .afterCursor(PageRequest.Cursor.forKey(
                                        oldestAdditionId,
                                        oldestAdditionTimestamp));

                p = repository.findChatroomUsersPage(chatroomId, pageRequest);

            }
        }

        return exPermission ?
                new ContentPage<>(
                        toDetails(p.content().stream()),
                        p.hasPrevious())
                : new ContentPage<>(
                toLimitedDetails(p.content().stream()),
                p.hasPrevious());

    }

}
