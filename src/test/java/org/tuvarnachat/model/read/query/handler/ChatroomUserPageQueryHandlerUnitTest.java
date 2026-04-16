package org.tuvarnachat.model.read.query.handler;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.read.query.PageQuery;
import org.tuvarna.chat.model.read.query.handler.impl.ChatroomUserPageQueryHandler;
import org.tuvarna.chat.model.read.query.page.data.ChatroomUserPageData;
import org.tuvarnachat.model.repository.mocks.ChatroomUsersReadMock;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ChatroomUserPageQueryHandlerUnitTest {

    @Inject
    ChatroomUserPageQueryHandler handler;

    @Inject
    ChatroomUsersReadMock repository;


    @Test
    void handleQuery_firstPage_withExtendedPermission_returnsAllUsers() {
        int chatroomId = 1;

        var query = new PageQuery.GetPage<>(
                chatroomId,
                new ChatroomUserPageData(null, null, true)
        );

        ContentPage<ChatroomUserDetails> result = handler.handleQuery(query);

        assertNotNull(result);
        assertEquals(25, result.content().size()); // PAGE_SIZE
        assertFalse(result.hasFollowing());

        // verify mapping
        ChatroomUserDetails first = result.content().get(0);
        assertEquals(chatroomId, first.chatroomId());
        assertEquals("ACTIVE", first.status());
    }

    @Test
    void handleQuery_firstPage_withoutExtendedPermission_returnsOnlyActiveUsers() {
        int chatroomId = 1;

        var query = new PageQuery.GetPage<>(
                chatroomId,
                new ChatroomUserPageData(null, null, false)
        );

        ContentPage<ChatroomUserDetails> result = handler.handleQuery(query);

        assertNotNull(result);

        assertEquals(25, result.content().size());

        // validate filtering logic explicitly
        assertTrue(result.content().stream()
                .allMatch(u -> u.status().equals("ACTIVE")));
    }


    @Test
    void handleQuery_withCursor_usesCursorPagination() {
        int chatroomId = 1;

        var query = new PageQuery.GetPage<>(
                chatroomId,
                new ChatroomUserPageData(
                        Instant.now(),
                        10,
                        true
                )
        );

        ContentPage<ChatroomUserDetails> result = handler.handleQuery(query);

        assertNotNull(result);
        assertEquals(25, result.content().size());
    }

    @Test
    void handleQuery_partialCursor_nullTimestamp_createsFirstPage() {
        int chatroomId = 1;

        var query = new PageQuery.GetPage<>(
                chatroomId,
                new ChatroomUserPageData(
                        null,
                        10,
                        true
                )
        );

        ContentPage<ChatroomUserDetails> result = handler.handleQuery(query);

        // falls back to first page
        assertEquals(25, result.content().size());
    }

    @Test
    void handleQuery_partialCursor_nullId_createsFirstPage() {
        int chatroomId = 1;

        var query = new PageQuery.GetPage<>(
                chatroomId,
                new ChatroomUserPageData(
                        Instant.now(),
                        null,
                        true
                )
        );

        ContentPage<ChatroomUserDetails> result = handler.handleQuery(query);

        assertEquals(25, result.content().size());
    }


    @Test
    void handleQuery_mapsAllFieldsCorrectly() {
        int chatroomId = 1;

        var query = new PageQuery.GetPage<>(
                chatroomId,
                new ChatroomUserPageData(null, null, true)
        );

        ContentPage<ChatroomUserDetails> result = handler.handleQuery(query);

        ChatroomUserDetails user = result.content().get(0);

        assertNotNull(user.id());
        assertEquals(chatroomId, user.chatroomId());
        assertNotNull(user.userId());
        assertEquals("MEMBER", user.role());
        assertEquals("ACTIVE", user.status());
        assertEquals(0L, user.lastRead());
    }

//    @Test
//    void handleQuery_emptyPage_returnsEmptyList() {
//        ChatroomUsersRead emptyRepo = new ChatroomUsersReadMock() {
//            @Override
//            public CursoredPage<ChatroomUser> findChatroomUsersPage(int chatroomId, PageRequest pageRequest) {
//                return new CursoredPageRecord<>(
//                        List.of(),
//                        List.of(),
//                        0,
//                        pageRequest,
//                        true,
//                        false
//                );
//            }
//        };
//
//        ChatroomUserPageQueryHandler localHandler =
//                new ChatroomUserPageQueryHandler(emptyRepo);
//
//        var query = new PageQuery.GetPage<>(
//                1,
//                new ChatroomUserPageData(null, null, true)
//        );
//
//        ContentPage<ChatroomUserDetails> result = localHandler.handleQuery(query);
//
//        assertNotNull(result);
//        assertTrue(result.content().isEmpty());
//    }

}