package org.tuvarna.chat.application.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.util.Map;

public interface ChatroomUserResource {

    @GET
    @Path("/{chatroomId}/users")
    ContentPage<ChatroomUserDetails> getUsers(
            @QueryParam("userId") @Positive long requestingUserId,
            @QueryParam("oldestAdditionTimestamp") String oldestAdditionTimestamp,
            @QueryParam("oldestAdditionId") Integer oldestAdditionId,
            @PathParam("chatroomId") @Positive int chatroomId);

    @POST
    @Path("/{chatroomId}/users")
    Response addUsers(
            @QueryParam("userId") @Positive long requestingUserId,
            @PathParam("chatroomId") @Positive int chatroomId,
            @Valid @NotNull AddUsersRequest request);

    @PUT
    @Path("/{chatroomId}/users/{affectedUserId}/role")
    Response changeUserRole(
            @QueryParam("userId") @Positive long requestingUserId,
            @PathParam("chatroomId") @Positive int chatroomId,
            @PathParam("affectedUserId") @Positive long affectedUserId,
            @Valid @NotNull UpdateRoleRequest request);

    @PUT
    @Path("/{chatroomId}/users/membership-status")
    Response changeUserMembershipStatus(
            @QueryParam("userId") @Positive long requestingUserId,
            @QueryParam("affectedUserId") @Positive long affectedUserId,
            @PathParam("chatroomId") @Positive int chatroomId,
            @Valid @NotNull UpdateMembershipStatusRequest request);

    @PUT
    @Path("/{chatroomId}/users/update-last-read")
    Response updateLastRead(
            @PathParam("chatroomId") @Positive int chatroomId,
            @QueryParam("userId") @Positive long userId,
            @Valid @NotNull UpdateLastReadStateRequest request);

    record UpdateLastReadStateRequest(

            @PositiveOrZero
            long newLastReadState
    ) {
    }

    record AddUsersRequest(

            @NotNull
            @Size(min = 1)
            Map<
                    @NotNull @Positive Long,
                    @NotBlank String> usersWithRoles
    ) {
    }

    record UpdateRoleRequest(

            @NotBlank
            String updatedRole
    ) {
    }

    record UpdateMembershipStatusRequest(

            @NotBlank
            String updatedStatus
    ) {
    }
}
