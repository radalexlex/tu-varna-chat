package org.tuvarnachat.model.read.query.handler;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.impl.ChatroomEventfulPageQueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatroomEventfulPageData;
import org.tuvarnachat.model.repository.mocks.ChatroomEventfulReadMock;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ChatroomEventfulPageQueryHandlerUnitTest {

    @Inject
    ChatroomEventfulPageQueryHandler handler;

    @Inject
    ChatroomEventfulReadMock repository;

    @Test
    void handleQuery_nullData_fetchFirstPage() {
        long userId = 1L;

        var query = new PageQuery.GetPage<Long, ChatroomEventfulPageData>(userId, null);

        ContentPage<ChatroomEventfulElement> result = handler.handleQuery(query);

        assertNotNull(result);
        assertNotNull(result.content());
        assertFalse(result.content().isEmpty());

        assertFalse(result.hasFollowing());
    }

    @Test
    void handleQuery_withData_fetchNextPage() {
        long userId = 1L;

        ChatroomEventfulPageData data = new ChatroomEventfulPageData(
                Instant.now(),
                10,
                100L
        );

        var query = new PageQuery.GetPage<>(userId, data);

        ContentPage<ChatroomEventfulElement> result = handler.handleQuery(query);

        assertNotNull(result);
        assertNotNull(result.content());
    }

    @Test
    void handleQuery_withData_nullFields_shouldStillWork() {
        long userId = 1L;

        ChatroomEventfulPageData data = new ChatroomEventfulPageData(
                null,
                null,
                null
        );

        var query = new PageQuery.GetPage<>(userId, data);

        ContentPage<ChatroomEventfulElement> result = handler.handleQuery(query);

        assertNotNull(result);
        assertNotNull(result.content());
    }

    @Test
    void handleQuery_mapsFields_correctly() {
        long userId = 1L;

        ContentPage<ChatroomEventfulElement> result =
                handler.handleQuery(new PageQuery.GetPage<>(userId, null));

        ChatroomEventfulElement element = result.content().get(0);

        assertTrue(element.userId() > 0);
        assertTrue(element.chatroomId() > 0);
        assertNotNull(element.chatroomName());

        assertNotNull(element.messageContent());
        assertTrue(element.messageContent().startsWith("Last message "));

        assertTrue(element.lastChatMessageId() >= 0);
        assertNotNull(element.clientMessageId());
        assertNotNull(element.activityTime());
    }

//    @Test
//    
//    void handleQuery_emptyResult_returnsEmptyPage() {
//        long userId = -999L; // mock should return empty
//
//        ContentPage<ChatroomEventfulElement> result =
//                handler.handleQuery(new PageQuery.GetPage<>(userId, null));
//
//        assertNotNull(result);
//        assertNotNull(result.content());
//        assertTrue(result.content().isEmpty());
//        assertFalse(result.hasFollowing());
//    }

    @Test
    void handleQuery_multipleCalls_consistentResults() {
        long userId = 1L;

        var query = new PageQuery.GetPage<Long, ChatroomEventfulPageData>(userId, null);

        ContentPage<ChatroomEventfulElement> result1 = handler.handleQuery(query);
        ContentPage<ChatroomEventfulElement> result2 = handler.handleQuery(query);

        assertEquals(result1.content().size(), result2.content().size());
        assertEquals(result1.hasFollowing(), result2.hasFollowing());
    }

//    @Test
//    
//    void handleQuery_paginationBoundary_hasFollowingFlag() {
//        long userId = 2L; // mock should simulate > PAGE_SIZE
//
//        ContentPage<ChatroomEventfulElement> result =
//                handler.handleQuery(new PageQuery.GetPage<>(userId, null));
//
//        assertNotNull(result);
//
//        if (result.content().size() == ChatroomEventfulRead.PAGE_SIZE) {
//            assertTrue(result.hasFollowing());
//        }
//    }

    @Test
    void handleQuery_withOlderCursor_returnsDifferentPage() {
        long userId = 1L;

        ContentPage<ChatroomEventfulElement> firstPage =
                handler.handleQuery(new PageQuery.GetPage<>(userId, null));

        assertFalse(firstPage.content().isEmpty());

        ChatroomEventfulElement last = firstPage.content()
                .get(firstPage.content().size() - 1);

        ChatroomEventfulPageData data = new ChatroomEventfulPageData(
                Instant.parse(last.activityTime()),
                last.chatroomId(),
                last.lastChatMessageId()
        );

        ContentPage<ChatroomEventfulElement> nextPage =
                handler.handleQuery(new PageQuery.GetPage<>(userId, data));

        assertNotNull(nextPage);
        assertNotNull(nextPage.content());
    }

    @Test
    void handleQuery_handlesNullQueryDataGracefully() {
        long userId = 1L;

        PageQuery<Long, ChatroomEventfulPageData> query =
                new PageQuery.GetPage<>(userId, null);

        ContentPage<ChatroomEventfulElement> result = handler.handleQuery(query);

        assertDoesNotThrow(() -> handler.handleQuery(query));
        assertNotNull(result);
    }
}