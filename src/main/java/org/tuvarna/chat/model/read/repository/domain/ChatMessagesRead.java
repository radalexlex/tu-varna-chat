package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;

@Repository
public interface ChatMessagesRead extends ReadRepository<ChatMessage, Long> {

    @Query("from ChatMessage where chatroomId = ?1 AND deleted = false")
    @OrderBy(value = "timeSent", descending = true)
    @OrderBy(value = "id", descending = true)
    CursoredPage<ChatMessage> findChatMessagesPage(int chatroomId,
                                                   PageRequest pageRequest);

}
