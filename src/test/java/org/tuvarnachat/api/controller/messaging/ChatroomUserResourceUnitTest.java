package org.tuvarnachat.api.controller.messaging;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.tuvarna.chat.application.api.controller.ChatroomUserResource;
import org.tuvarna.chat.application.api.service.ChatroomUserService;
import org.tuvarna.chat.application.exceptions.persistence.missing.ChatroomUserMissingException;
import org.tuvarna.chat.application.exceptions.service.ChatroomUserServiceException;
import org.tuvarna.chat.application.exceptions.validation.room.InvalidChatroomIdException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.time.Instant;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ChatroomUserResourceUnitTest {

    @InjectMock
    ChatroomUserService chatroomUserService;

    @Test
    void getUsers_ok_withTimestamp() {
        ContentPage<ChatroomUserDetails> page = mock(ContentPage.class);
        String ts = "2026-01-01T10:00:00Z";

        when(chatroomUserService.getUsers(
                eq(1L),
                eq(10),
                eq(Instant.parse(ts)),
                eq(5)
        )).thenReturn(page);

        given()
                .queryParam("userId", 1)
                .queryParam("oldestAdditionTimestamp", ts)
                .queryParam("oldestAdditionId", 5)
                .when()
                .get("/chatroom-users/10/users")
                .then()
                .statusCode(200);

        verify(chatroomUserService).getUsers(
                eq(1L),
                eq(10),
                eq(Instant.parse(ts)),
                eq(5)
        );
    }

    @Test
    void getUsers_ok_nullTimestamp() {
        ContentPage<ChatroomUserDetails> page = mock(ContentPage.class);

        when(chatroomUserService.getUsers(
                eq(1L),
                eq(10),
                isNull(),
                isNull()
        )).thenReturn(page);

        given()
                .queryParam("userId", 1)
                .when()
                .get("/chatroom-users/10/users")
                .then()
                .statusCode(200);

        verify(chatroomUserService).getUsers(
                eq(1L),
                eq(10),
                isNull(),
                isNull()
        );
    }

    @Test
    void getUsers_invalidTimestamp() {
        given()
                .queryParam("userId", 1)
                .queryParam("oldestAdditionTimestamp", "invalid")
                .when()
                .get("/chatroom-users/10/users")
                .then()
                .statusCode(400);
    }

    @Test
    void getUsers_validationFail_negativeUserId() {
        given()
                .queryParam("userId", -1)
                .when()
                .get("/chatroom-users/10/users")
                .then()
                .statusCode(400);
    }

    @Test
    void getUsers_fromAdvice_forbidden() {
        when(chatroomUserService.getUsers(anyLong(), anyInt(), any(), any()))
                .thenThrow(new ChatroomUserServiceException(
                        new UserNotAllowedException("forbidden")
                ));

        given()
                .queryParam("userId", 1)
                .when()
                .get("/chatroom-users/10/users")
                .then()
                .statusCode(403);
    }


    @Test
    void addUsers_ok() {
        Map<Long, String> users = Map.of(2L, "admin");

        ChatroomUserResource.AddUsersRequest request = new ChatroomUserResource.AddUsersRequest(users);

        when(chatroomUserService.addUsers(eq(1L), any()))
                .thenReturn(2);

        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/chatroom-users/10/users")
                .then()
                .statusCode(201)
                .body(is("2"));

        verify(chatroomUserService).addUsers(eq(1L), any());
    }

    @Test
    void addUsers_invalidRole() {
        Map<Long, String> users = Map.of(2L, "invalid-role");

        ChatroomUserResource.AddUsersRequest request = new ChatroomUserResource.AddUsersRequest(users);

        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/chatroom-users/10/users")
                .then()
                .statusCode(400);
    }

    @Test
    void addUsers_nullBody() {
        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body("")
                .when()
                .post("/chatroom-users/10/users")
                .then()
                .statusCode(400);
    }

    @Test
    void addUsers_nullMap() {
        ChatroomUserResource.AddUsersRequest request = new ChatroomUserResource.AddUsersRequest(null);

        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/chatroom-users/10/users")
                .then()
                .statusCode(400);
    }

    @Test
    void addUsers_fromAdvice_badRequest() {
        Map<Long, String> users = Map.of(2L, "admin");

        when(chatroomUserService.addUsers(anyLong(), any()))
                .thenThrow(new ChatroomUserServiceException(
                        new InvalidChatroomIdException("bad")
                ));

        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body(new ChatroomUserResource.AddUsersRequest(users))
                .when()
                .post("/chatroom-users/10/users")
                .then()
                .statusCode(400);
    }


    @Test
    void changeUserRole_ok() {
        ChatroomUserResource.UpdateRoleRequest request = new ChatroomUserResource.UpdateRoleRequest("admin");

        when(chatroomUserService.changeUserRole(1L, 10, 2L, "admin"))
                .thenReturn(1);

        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chatroom-users/10/users/2/role")
                .then()
                .statusCode(200)
                .body(is("1"));

        verify(chatroomUserService)
                .changeUserRole(1L, 10, 2L, "admin");
    }

    @Test
    void changeUserRole_invalidRole() {
        ChatroomUserResource.UpdateRoleRequest request = new ChatroomUserResource.UpdateRoleRequest("invalid");

        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chatroom-users/10/users/2/role")
                .then()
                .statusCode(400);
    }

    @Test
    void changeUserRole_nullBody() {
        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body("")
                .when()
                .put("/chatroom-users/10/users/2/role")
                .then()
                .statusCode(400);
    }


    @Test
    void changeMembershipStatus_ok() {
        ChatroomUserResource.UpdateMembershipStatusRequest request =
                new ChatroomUserResource.UpdateMembershipStatusRequest("active");

        when(chatroomUserService.changeUserMembershipStatus(1L, 10, 2L, "active"))
                .thenReturn(1);

        given()
                .queryParam("userId", 1)
                .queryParam("affectedUserId", 2)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chatroom-users/10/users/membership-status")
                .then()
                .statusCode(200)
                .body(is("1"));

        verify(chatroomUserService)
                .changeUserMembershipStatus(1L, 10, 2L, "active");
    }

    @Test
    void changeMembershipStatus_invalidStatus() {
        ChatroomUserResource.UpdateMembershipStatusRequest request =
                new ChatroomUserResource.UpdateMembershipStatusRequest("invalid");

        given()
                .queryParam("userId", 1)
                .queryParam("affectedUserId", 2)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chatroom-users/10/users/membership-status")
                .then()
                .statusCode(400);
    }


    @Test
    void updateLastRead_ok() {
        ChatroomUserResource.UpdateLastReadStateRequest request =
                new ChatroomUserResource.UpdateLastReadStateRequest(100L);

        when(chatroomUserService.updateLastReadStatus(1L, 10, 100L))
                .thenReturn(1);

        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/chatroom-users/10/users/update-last-read")
                .then()
                .statusCode(200)
                .body(is("1"));

        verify(chatroomUserService)
                .updateLastReadStatus(1L, 10, 100L);
    }

    @Test
    void updateLastRead_nullBody() {
        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body("")
                .when()
                .put("/chatroom-users/10/users/update-last-read")
                .then()
                .statusCode(400);
    }


    @Test
    void changeMembershipStatus_fromAdvice_notFound() {
        when(chatroomUserService.changeUserMembershipStatus(
                anyLong(), anyInt(), anyLong(), anyString()
        )).thenThrow(new ChatroomUserServiceException(
                new ChatroomUserMissingException("missing")
        ));

        given()
                .queryParam("userId", 1)
                .queryParam("affectedUserId", 2)
                .contentType(ContentType.JSON)
                .body(new ChatroomUserResource.UpdateMembershipStatusRequest("active"))
                .when()
                .put("/chatroom-users/10/users/membership-status")
                .then()
                .statusCode(404);
    }

    @Test
    void updateLastRead_fromAdvice_internalError() {
        when(chatroomUserService.updateLastReadStatus(
                anyLong(), anyInt(), anyLong()
        )).thenThrow(new RuntimeException("boom"));

        given()
                .queryParam("userId", 1)
                .contentType(ContentType.JSON)
                .body(new ChatroomUserResource.UpdateLastReadStateRequest(10L))
                .when()
                .put("/chatroom-users/10/users/update-last-read")
                .then()
                .statusCode(500);
    }
}