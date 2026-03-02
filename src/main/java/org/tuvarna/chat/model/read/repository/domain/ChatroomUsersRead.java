package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.*;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatroomUsersRead {

    @Find
    Optional<ChatroomUser> findById(@By("id(this)") Long id);

    @Query("from ChatroomUser where chatroomId = ?1 ")
    @OrderBy(value = "joinTime", descending = true)
    @OrderBy(value = "id", descending = true)
    CursoredPage<ChatroomUser> findChatroomUsersPage(int chatroomId,
                                                     PageRequest pageRequest);

    @Query("from ChatroomUser where userId = ?1")
    List<ChatroomUser> getUserByUserId(long userId);

    @Query("from ChatroomUser where chatroomId = ?1")
    List<ChatroomUser> getUsersByChatroomId(int chatroomId);

}
