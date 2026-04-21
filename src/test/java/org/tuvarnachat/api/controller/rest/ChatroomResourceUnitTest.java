package org.tuvarnachat.api.controller.rest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.api.controller.ChatroomResource;
import org.tuvarna.chat.application.api.service.ChatroomService;
import org.tuvarna.chat.application.exceptions.conflict.room.ChatroomAlreadyDeletedException;
import org.tuvarna.chat.application.exceptions.notfound.ChatroomNotFoundException;
import org.tuvarna.chat.application.exceptions.service.ChatroomServiceException;
import org.tuvarna.chat.application.exceptions.validation.room.InvalidChatroomNameException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ChatroomResourceUnitTest {

    private static final Logger log = LoggerFactory.getLogger(ChatroomResourceUnitTest.class);

    @InjectMock
    ChatroomService chatroomService;

    @Test
    void createChatroom_ok() {
        ChatroomResource.CreateChatroomRequest request =
                new ChatroomResource.CreateChatroomRequest(123L, "test-room");

        when(chatroomService.createChatroom(123L, "test-room"))
                .thenReturn(42);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/chatrooms/create")
                .then()
                .statusCode(200)
                .body(is("42"));

        verify(chatroomService).createChatroom(123L, "test-room");
    }

    @Test
    void createChatroom_invalidRequest_emptyBody() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/chatrooms/create")
                .then()
                .statusCode(400);
    }

    @Test
    void createChatroom_invalidRequest_nullBody() {
        given()
                .contentType(ContentType.JSON)
                .body("")
                .when()
                .post("/chatrooms/create")
                .then()
                .statusCode(400);
    }

    @Test
    void createChatroom_invalidName_blank() {
        ChatroomResource.CreateChatroomRequest request =
                new ChatroomResource.CreateChatroomRequest(123L, " ");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/chatrooms/create")
                .then()
                .statusCode(400);
    }

    @Test
    void archiveChatroom_ok() {
        when(chatroomService.archiveChatroom(1L, 10)).thenReturn(1);

        given()
                .queryParam("userId", 1)
                .when()
                .put("/chatrooms/10/archive")
                .then()
                .statusCode(200)
                .body(is("1"));

        verify(chatroomService).archiveChatroom(1L, 10);
    }

    @Test
    void getChatroomOverview_ok() {
        ChatroomOverview mockOverview = mock(ChatroomOverview.class);

        when(chatroomService.getChatroomOverview(1L, 10))
                .thenReturn(mockOverview);

        given()
                .queryParam("userId", 1)
                .when()
                .get("/chatrooms/10")
                .then()
                .statusCode(200);

        verify(chatroomService).getChatroomOverview(1L, 10);
    }

    @Test
    void getChatroomEvents_withTimestamp() {
        ContentPage<ChatroomEventfulElement> page = mock(ContentPage.class);

        String timestamp = "2026-01-01T10:00:00Z";

        when(chatroomService.getChatroomEventfulElements(
                eq(1L),
                eq(Instant.parse(timestamp)),
                eq(5),
                eq(100L)
        )).thenReturn(page);

        given()
                .queryParam("userId", 1)
                .queryParam("latestEventTimeOnPage", timestamp)
                .queryParam("latestChatroomIdOnPage", 5)
                .queryParam("latestChatMessageIdOnPage", 100)
                .when()
                .get("/chatrooms/events")
                .then()
                .statusCode(200);

        verify(chatroomService).getChatroomEventfulElements(
                eq(1L),
                eq(Instant.parse(timestamp)),
                eq(5),
                eq(100L)
        );
    }

    @Test
    void getChatroomEvents_nullTimestamp() {
        ContentPage<ChatroomEventfulElement> page = mock(ContentPage.class);

        when(chatroomService.getChatroomEventfulElements(
                eq(1L),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(page);

        given()
                .queryParam("userId", 1)
                .when()
                .get("/chatrooms/events")
                .then()
                .statusCode(200);

        verify(chatroomService).getChatroomEventfulElements(
                eq(1L),
                isNull(),
                isNull(),
                isNull()
        );
    }

    @Test
    void getChatroomIdsForUser_ok() {
        when(chatroomService.getChatroomIdsForUser(1L))
                .thenReturn(List.of(1, 2, 3));

        given()
                .queryParam("userId", 1)
                .when()
                .get("/chatrooms/user")
                .then()
                .statusCode(200)
                .body("$", hasSize(3));

        verify(chatroomService).getChatroomIdsForUser(1L);
    }

    @Test
    void updateChatroomName_ok() {
        when(chatroomService.updateChatroomName(1L, 10, "new"))
                .thenReturn(1);

        given()
                .queryParam("userId", 1)
                .queryParam("newName", "new")
                .when()
                .put("/chatrooms/10/name")
                .then()
                .statusCode(200)
                .body(is("1"));

        verify(chatroomService).updateChatroomName(1L, 10, "new");
    }

    @Test
    void updateLastRead_ok() {
        when(chatroomService.updateLastReadStatus(1L, 10, 100L))
                .thenReturn(1);

        given()
                .queryParam("userId", 1)
                .queryParam("newLastReadState", 100)
                .when()
                .put("/chatrooms/10/update-last-read")
                .then()
                .statusCode(200);

        verify(chatroomService).updateLastReadStatus(1L, 10, 100L);
    }

    @Test
    void updateChatroomLastRead_ok() {
        when(chatroomService.updateLastReadStatus(1L, 10, 200L))
                .thenReturn(1);

        given()
                .queryParam("userId", 1)
                .queryParam("newLastRead", 200)
                .when()
                .put("/chatrooms/10/last-read")
                .then()
                .statusCode(200);

        verify(chatroomService).updateLastReadStatus(1L, 10, 200L);
    }

    //validation tests
    @Test
    void createChatroom_forbidden_userNotAllowed() {
        when(chatroomService.createChatroom(anyLong(), anyString()))
                .thenThrow(new ChatroomServiceException(
                        new UserNotAllowedException("Forbidden")
                ));

        given()
                .contentType(ContentType.JSON)
                .body(new ChatroomResource.CreateChatroomRequest(1L, "test"))
                .when()
                .post("/chatrooms/create")
                .then()
                .statusCode(403)
                .body(containsString("Forbidden"));
    }

    @Test
    void createChatroom_invalidName_exceptionMappedTo400() {
        when(chatroomService.createChatroom(anyLong(), anyString()))
                .thenThrow(new ChatroomServiceException(
                        new InvalidChatroomNameException("Invalid name")
                ));

        given()
                .contentType(ContentType.JSON)
                .body(new ChatroomResource.CreateChatroomRequest(1L, "bad"))
                .when()
                .post("/chatrooms/create")
                .then()
                .statusCode(400);
    }

    @Test
    void getChatroomOverview_notFound() {
        when(chatroomService.getChatroomOverview(anyLong(), anyInt()))
                .thenThrow(new ChatroomServiceException(
                        new ChatroomNotFoundException("Missing")
                ));

        given()
                .queryParam("userId", 1)
                .when()
                .get("/chatrooms/99")
                .then()
                .statusCode(404);
    }

    @Test
    void archiveChatroom_conflict_deleted() {
        when(chatroomService.archiveChatroom(anyLong(), anyInt()))
                .thenThrow(new ChatroomServiceException(
                        new ChatroomAlreadyDeletedException("Already deleted")
                ));

        given()
                .queryParam("userId", 1)
                .when()
                .put("/chatrooms/10/archive")
                .then()
                .statusCode(409);
    }

    @Test
    void createChatroom_unexpectedError() {
        when(chatroomService.createChatroom(anyLong(), anyString()))
                .thenThrow(new ChatroomServiceException(new RuntimeException("boom")));

        given()
                .contentType(ContentType.JSON)
                .body(new ChatroomResource.CreateChatroomRequest(1L, "test"))
                .when()
                .post("/chatrooms/create")
                .then()
                .statusCode(500);
    }

    @Test
    void getEvents_invalidTimestamp_format() {
        given()
                .queryParam("userId", 1)
                .queryParam("latestEventTimeOnPage", "invalid-date")
                .when()
                .get("/chatrooms/events")
                .then()
                .statusCode(400);
    }

    @Test
    void archiveChatroom_missingUserId() {
        given()
                .when()
                .put("/chatrooms/10/archive")
                .then()
                .statusCode(400);
    }

    @Test
    void archiveChatroom_negativeUserId() {
        given()
                .queryParam("userId", -1)
                .when()
                .put("/chatrooms/10/archive")
                .then()
                .statusCode(400);
    }

    @Test
    void getEvents_onlyUserId() {
        when(chatroomService.getChatroomEventfulElements(
                eq(1L), isNull(), isNull(), isNull()))
                .thenReturn(mock(ContentPage.class));

        given()
                .queryParam("userId", 1)
                .when()
                .get("/chatrooms/events")
                .then()
                .statusCode(200);
    }

    @Test
    void updateChatroomName_nullName_passedToService() {
        when(chatroomService.updateChatroomName(1L, 10, null))
                .thenThrow(InvalidChatroomNameException.class);

        given()
                .queryParam("userId", 1)
                .when()
                .put("/chatrooms/10/name")
                .then()
                .statusCode(400);

//        verify(chatroomService).updateChatroomName(1L, 10, null);
    }

}
//
//    @Test
//    public void testCreateChatroomOk() {
//
//        ChatroomResource.CreateChatroomRequest r
//                = new ChatroomResource.CreateChatroomRequest(
//                        123L, "aaaaa");
//
//        when(chatroomService.createChatroom(eq(r.requestingUserId()), eq(r.name()))).thenReturn(1);
//
//        Response resp = given()
//                .request()
//                .body(r)
//                .contentType(ContentType.JSON)
//                .when()
//                .post("/chatrooms/create");
//
//        log.info(resp.body().prettyPrint());
//
//        assertThat(resp.statusCode(), is(200));
//        assertThat(resp.jsonPath().getInt("$"), is(1));
//
//    }
//@Test
//    public void testHello() {
//        ChatroomResource.CreateChatroomRequest req = new ChatroomResource.CreateChatroomRequest(1L, "aaa");
//
//        Response resp = given()
//                .request()
//                .body(req)
//                .contentType(ContentType.JSON)
//                .when()
//                .post("/chatrooms/hello");
//
//            assertThat(resp.jsonPath().getLong("requestingUserId"), is(req.requestingUserId()));
//            assertThat(resp.jsonPath().getString("name"), is(req.name()));
//    }