package org.tuvarnachat.api.controller.messaging;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.application.api.controller.ChatMessageResource;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.persistence.DataPersistenceException;
import org.tuvarna.chat.application.exceptions.persistence.missing.ChatMessageMissingException;
import org.tuvarna.chat.application.exceptions.service.ChatMessageServiceException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotChatroomMemberException;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ChatMessageResourceUnitTest {

    @InjectMock
    ChatMessageService chatMessageService;


    @Test
    void updateMessage_ok() {
        ChatMessageResource.MessageUpdateRequest request = new ChatMessageResource.MessageUpdateRequest(
                1L,
                10,
                mock(ChatMessageElement.class),
                "updated"
        );

        when(chatMessageService.updateMessage(anyLong(), any(), anyInt(), anyString()))
                .thenReturn(1);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chat-messages/update")
                .then()
                .statusCode(200);

        verify(chatMessageService)
                .updateMessage(eq(1L), any(), eq(10), eq("updated"));
    }

    @Test
    void updateMessage_badRequest_whenServiceReturns0() {
        ChatMessageResource.MessageUpdateRequest request = new ChatMessageResource.MessageUpdateRequest(
                1L,
                10,
                mock(ChatMessageElement.class),
                "updated"
        );

        when(chatMessageService.updateMessage(anyLong(), any(), anyInt(), anyString()))
                .thenReturn(0);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chat-messages/update")
                .then()
                .statusCode(400);
    }

    @Test
    void updateMessage_validationFail_emptyBody() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .put("/chat-messages/update")
                .then()
                .statusCode(400);
    }

    @Test
    void updateMessage_validationFail_nullBody() {
        given()
                .contentType(ContentType.JSON)
                .body("")
                .when()
                .put("/chat-messages/update")
                .then()
                .statusCode(400);
    }

    @Test
    void updateMessage_validationFail_blankMessage() {
        ChatMessageResource.MessageUpdateRequest request = new ChatMessageResource.MessageUpdateRequest(
                1L,
                10,
                mock(ChatMessageElement.class),
                ""
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chat-messages/update")
                .then()
                .statusCode(400);
    }

    @Test
    void updateMessage_forbidden_fromAdvice() {
        ChatMessageResource.MessageUpdateRequest request = new ChatMessageResource.MessageUpdateRequest(
                1L,
                10,
                mock(ChatMessageElement.class),
                "updated"
        );

        when(chatMessageService.updateMessage(anyLong(), any(), anyInt(), anyString()))
                .thenThrow(new ChatMessageServiceException(
                        new UserNotAllowedException("forbidden")
                ));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chat-messages/update")
                .then()
                .statusCode(403);
    }

    @Test
    void updateMessage_notFound_fromAdvice() {
        ChatMessageResource.MessageUpdateRequest request = new ChatMessageResource.MessageUpdateRequest(
                1L,
                10,
                mock(ChatMessageElement.class),
                "updated"
        );

        when(chatMessageService.updateMessage(anyLong(), any(), anyInt(), anyString()))
                .thenThrow(new ChatMessageServiceException(
                        new ChatMessageMissingException("missing")
                ));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chat-messages/update")
                .then()
                .statusCode(404);
    }

    @Test
    void archiveMessage_ok() {
        ChatMessageResource.MessageRemoveRequest request = new ChatMessageResource.MessageRemoveRequest(
                1L,
                10,
                mock(ChatMessageElement.class)
        );

        when(chatMessageService.archiveMessage(anyLong(), any(), anyInt()))
                .thenReturn(1);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chat-messages/archive")
                .then()
                .statusCode(200);

        verify(chatMessageService)
                .archiveMessage(eq(1L), any(), eq(10));
    }

    @Test
    void archiveMessage_badRequest_whenServiceReturns0() {
        ChatMessageResource.MessageRemoveRequest request = new ChatMessageResource.MessageRemoveRequest(
                1L,
                10,
                mock(ChatMessageElement.class)
        );

        when(chatMessageService.archiveMessage(anyLong(), any(), anyInt()))
                .thenReturn(0);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chat-messages/archive")
                .then()
                .statusCode(400);
    }

    @Test
    void archiveMessage_validationFail_emptyBody() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .put("/chat-messages/archive")
                .then()
                .statusCode(400);
    }

    @Test
    void archiveMessage_forbidden_fromAdvice() {
        ChatMessageResource.MessageRemoveRequest request = new ChatMessageResource.MessageRemoveRequest(
                1L,
                10,
                mock(ChatMessageElement.class)
        );

        when(chatMessageService.archiveMessage(anyLong(), any(), anyInt()))
                .thenThrow(new ChatMessageServiceException(
                        new UserNotChatroomMemberException("forbidden")
                ));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chat-messages/archive")
                .then()
                .statusCode(403);
    }

    @Test
    void getMessagePage_ok_withTimestamp() {
        ContentPage<ChatMessageElement> page = mock(ContentPage.class);

        String ts = "2026-01-01T10:00:00Z";

        when(chatMessageService.getMessagePage(
                eq(1L),
                eq(10),
                eq(Instant.parse(ts)),
                eq(5),
                eq(true)
        )).thenReturn(page);

        given()
                .queryParam("requesterUserId", 1)
                .queryParam("chatroomId", 10)
                .queryParam("oldestTimestamp", ts)
                .queryParam("oldestId", 5)
                .queryParam("requestForOlder", true)
                .when()
                .get("/chat-messages/page")
                .then()
                .statusCode(200);

        verify(chatMessageService).getMessagePage(
                eq(1L),
                eq(10),
                eq(Instant.parse(ts)),
                eq(5),
                eq(true)
        );
    }

    @Test
    void getMessagePage_ok_nullTimestamp() {
        ContentPage<ChatMessageElement> page = mock(ContentPage.class);


        given()
                .queryParam("requesterUserId", 1)
                .queryParam("chatroomId", 10)
                .queryParam("requestForOlder", false)
                .when()
                .get("/chat-messages/page")
                .then()
                .statusCode(204);

        verify(chatMessageService).getMessagePage(
                eq(1L),
                eq(10),
                isNull(),
                isNull(),
                eq(false)
        );
    }

    @Test
    void getMessagePage_invalidTimestamp_format() {
        given()
                .queryParam("requesterUserId", 1)
                .queryParam("chatroomId", 10)
                .queryParam("oldestTimestamp", "invalid")
                .when()
                .get("/chat-messages/page")
                .then()
                .statusCode(400);
    }

    @Test
    void getMessagePage_validationFail_negativeUserId() {
        given()
                .queryParam("requesterUserId", -1)
                .queryParam("chatroomId", 10)
                .when()
                .get("/chat-messages/page")
                .then()
                .statusCode(400);
    }

    @Test
    void getMessagePage_validationFail_negativeChatroomId() {
        given()
                .queryParam("requesterUserId", 1)
                .queryParam("chatroomId", -10)
                .when()
                .get("/chat-messages/page")
                .then()
                .statusCode(400);
    }

    @Test
    void getMessagePage_fromAdvice_badRequest() {
        when(chatMessageService.getMessagePage(
                anyLong(), anyInt(), any(), any(), anyBoolean()
        )).thenThrow(new ChatMessageServiceException(
                new PaginationException("bad pagination")
        ));

        given()
                .queryParam("requesterUserId", 1)
                .queryParam("chatroomId", 10)
                .when()
                .get("/chat-messages/page")
                .then()
                .statusCode(400);
    }

    @Test
    void getMessagePage_fromAdvice_internalError() {
        when(chatMessageService.getMessagePage(
                anyLong(), anyInt(), any(), any(), anyBoolean()
        )).thenThrow(new ChatMessageServiceException(
                new DataPersistenceException("db error")
        ));

        given()
                .queryParam("requesterUserId", 1)
                .queryParam("chatroomId", 10)
                .when()
                .get("/chat-messages/page")
                .then()
                .statusCode(500);
    }
}