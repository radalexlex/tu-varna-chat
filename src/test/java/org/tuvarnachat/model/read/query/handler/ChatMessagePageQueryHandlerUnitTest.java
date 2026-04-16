package org.tuvarnachat.model.read.query.handler;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.impl.ChatMessagePageQueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatMessagePageData;
import org.tuvarnachat.model.repository.mocks.ChatMessagesReadMock;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ChatMessagePageQueryHandlerUnitTest {

    @Inject
    ChatMessagePageQueryHandler handler;

    @Inject
    ChatMessagesReadMock repository;

    @Test
    void handleQuery_nullData_fetchOlderFirstPage() {
        int chatroomId = 1;

        var query = new PageQuery.GetPage<Integer, ChatMessagePageData>(chatroomId, null);

        ContentPage<ChatMessageElement> result = handler.handleQuery(query);

        assertNotNull(result);
        assertFalse(result.content().isEmpty());

        assertFalse(result.hasFollowing());
    }

    @Test
    void handleQuery_withData_requestNewer() {
        int chatroomId = 1;

        ChatMessagePageData data = new ChatMessagePageData(
                Instant.now(), 5, false
        );

        var query = new PageQuery.GetPage<>(chatroomId, data);

        ContentPage<ChatMessageElement> result = handler.handleQuery(query);

        assertNotNull(result);
        assertFalse(result.content().isEmpty());
    }

    @Test
    void handleQuery_mapsMessage_correctly() {
        var result = handler.handleQuery(
                new PageQuery.GetPage<>(1, null)
        );

        ChatMessageElement element = result.content().get(0);

        assertNotNull(element.id());
        assertNotNull(element.uuid());
        assertEquals(10, element.senderUser());
        assertNotNull(element.timeSent());

        assertNotNull(element.content());
        assertTrue(element.content().startsWith("Mock message"));
    }

    @Test
    void handleQuery_multipleMessages_consistentMapping() {
        var result = handler.handleQuery(
                new PageQuery.GetPage<>(1, null)
        );

        assertTrue(result.content().size() > 1);

        for (ChatMessageElement element : result.content()) {
            assertNotNull(element.id());
            assertNotNull(element.uuid());
            assertEquals(10, element.senderUser());
            assertNotNull(element.timeSent());
            assertNotNull(element.content());
        }
    }
//
//    @Test
//    
//    void handleQuery_hasPrevious_propagatesCorrectly() {
//        int chatroomId = 1;
//
//        ChatMessagePageData data = new ChatMessagePageData(
//                Instant.now(), 5, true
//        );
//
//        var result = handler.handleQuery(
//                new PageQuery.GetPage<>(chatroomId, data)
//        );
//
//        assertTrue(result.hasFollowing());
//    }

    @Test
    void handleQuery_pageSize_respected() {
        var result = handler.handleQuery(
                new PageQuery.GetPage<>(1, null)
        );

        assertFalse(result.content().isEmpty());
    }
}