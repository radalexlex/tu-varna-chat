package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.*;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessagesRead {

    @Find
    @Query("from ChatMessage where deleted = true and deleted = false")
    List<ChatMessage> forceListImport();

    @Find
    Optional<ChatMessage> findById(@By("id(this)") Long id);

    @Find
    @OrderBy(value = "timeSent", descending = true)
    @OrderBy(value = "id", descending = true)
    CursoredPage<ChatMessage> findByChatroomIdOlder(int chatroomId,
                                                    PageRequest pageRequest);

    @Find
    @OrderBy(value = "timeSent", descending = false)
    @OrderBy(value = "id", descending = false)
    CursoredPage<ChatMessage> findByChatroomIdNewer(int chatroomId,
                                                    PageRequest pageRequest);


}
