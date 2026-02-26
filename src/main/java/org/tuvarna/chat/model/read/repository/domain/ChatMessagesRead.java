package org.tuvarna.chat.model.read.repository.domain;

import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.*;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.entity.postgres.ChatMessage_;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Optional;
import java.util.List;

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
    CursoredPage<ChatMessage> findByChatroomIdAndDeleted(int chatroomId,
                                                         boolean deleted,
                                                       PageRequest pageRequest);


}
