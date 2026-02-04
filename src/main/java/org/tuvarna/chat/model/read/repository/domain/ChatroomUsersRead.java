package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import org.tuvarna.chat.model.entity.postgres.ChatroomUser;

@Repository
public interface ChatroomUsersRead extends ReadRepository<ChatroomUser, Integer> {

    @Query("from ChatroomUser where chatroomId = ?1 ")
    @OrderBy(value = "joinTime", descending = true)
    @OrderBy(value = "id", descending = true)
    CursoredPage<ChatroomUser> findChatroomUsersPage(int chatroomId,
                                                         PageRequest pageRequest);

}
