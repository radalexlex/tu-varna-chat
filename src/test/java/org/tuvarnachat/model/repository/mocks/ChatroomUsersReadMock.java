package org.tuvarnachat.model.repository.mocks;

import io.quarkus.test.Mock;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.page.impl.CursoredPageRecord;
import jakarta.enterprise.context.ApplicationScoped;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.read.repository.domain.ChatroomUsersRead;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mock
@ApplicationScoped
public class ChatroomUsersReadMock implements ChatroomUsersRead {

    @Override
    public Optional<ChatroomUser> findById(Long id) {
        return Optional.of(build(id, 1));
    }

    @Override
    public CursoredPage<ChatroomUser> findChatroomUsersPage(int chatroomId, PageRequest pageRequest) {

        int size = pageRequest.size();

        List<ChatroomUser> users = new ArrayList<>();
        List<PageRequest.Cursor> cursors = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            long id = i + 1;

            ChatroomUser u = build(id, chatroomId);
            users.add(u);

            cursors.add(PageRequest.Cursor.forKey(u.getJoinTime(), u.getId()));
        }

        return new CursoredPageRecord<>(
                users,
                cursors,
                100,
                pageRequest,
                pageRequest.page() <= 1,
                true
        );
    }

    @Override
    public Optional<ChatroomUser> getUserByUserId(long userId) {
        return Optional.of(build(userId, 1));
    }

    @Override
    public List<ChatroomUser> getUsersByChatroomId(int chatroomId) {
        return List.of(
                build(1, chatroomId),
                build(2, chatroomId)
        );
    }

    @Override
    public Optional<ChatroomUser> getUserByUserIdAndChatroomId(long userId, int chatroomId) {
        return Optional.of(build(userId, chatroomId));
    }

    private ChatroomUser build(long userId, int chatroomId) {
        ChatroomUser u = new ChatroomUser();
        u.setId(userId);
        u.setUserId(userId);
        u.setChatroomId(chatroomId);
        u.setJoinTime(Instant.now().minusSeconds(userId * 100));
        u.setRole(ChatroomRole.MEMBER);
        u.setStatus(MembershipStatus.ACTIVE);
        u.setLastRead(0L);
        return u;
    }
}