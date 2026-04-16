package org.tuvarnachat.model.repository.mocks;

import io.quarkus.test.Mock;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.data.page.impl.CursoredPageRecord;
import jakarta.enterprise.context.ApplicationScoped;
import org.tuvarna.chat.model.entity.postgres.ChatMessage;
import org.tuvarna.chat.model.read.repository.domain.ChatMessagesRead;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mock
@ApplicationScoped
public class ChatMessagesReadMock implements ChatMessagesRead {

    @Override
    public List<ChatMessage> forceListImport() {
        return new ArrayList<>();
    }

    @Override
    public Optional<ChatMessage> findById(Long id) {
        ChatMessage cm = buildMessage(id);
        return Optional.of(cm);
    }

    @Override
    public CursoredPage<ChatMessage> findByChatroomIdOlder(int chatroomId, PageRequest pageRequest) {
        return buildPage(chatroomId, pageRequest, false);
    }

    @Override
    public CursoredPage<ChatMessage> findByChatroomIdNewer(int chatroomId, PageRequest pageRequest) {
        return buildPage(chatroomId, pageRequest, true);
    }


    private CursoredPage<ChatMessage> buildPage(int chatroomId, PageRequest pageRequest, boolean newer) {
        int size = pageRequest.size();

        List<ChatMessage> messages = new ArrayList<>();
        List<PageRequest.Cursor> cursors = new ArrayList<>();

        long baseId = newer ? 100 : 200;

        for (int i = 0; i < size; i++) {
            long id = baseId + i;

            ChatMessage msg = buildMessage(id);
            msg.setChatroomId(chatroomId);

            messages.add(msg);

            cursors.add(PageRequest.Cursor.forKey(msg.getTimeSent(), msg.getId()));
        }

        boolean firstPage = pageRequest.page() <= 1;
        boolean lastPage = true;

        return new CursoredPageRecord<>(
                messages,
                cursors,
                1000,
                pageRequest,
                firstPage,
                lastPage
        );
    }


    private ChatMessage buildMessage(long id) {
        ChatMessage cm = new ChatMessage();
        cm.setId(id);
        cm.setChatroomId(1);
        cm.setSenderUserId(10);
        cm.setContent("Mock message " + id);
        cm.setTimeSent(Instant.now().minusSeconds(id * 60));
        cm.setDeleted(false);
        cm.setClientMessageId(UUID.randomUUID());
        return cm;
    }
}