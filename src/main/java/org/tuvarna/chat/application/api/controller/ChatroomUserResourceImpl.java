package org.tuvarna.chat.application.api.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tuvarna.chat.application.api.service.ChatroomUserService;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
import org.tuvarna.chat.model.entity.postgres.enums.MembershipStatus;
import org.tuvarna.chat.model.read.dto.ChatroomUserDetails;
import org.tuvarna.chat.model.read.dto.ContentPage;
import org.tuvarna.chat.model.write.dto.ChatroomUsersSaveData;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/chatroom-users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatroomUserResourceImpl implements ChatroomUserResource {

    @Inject
    ChatroomUserService chatroomUserService;

    @GET
    @Path("/{chatroomId}/users")
    @Override
    public ContentPage<ChatroomUserDetails> getUsers(
            @QueryParam("userId") @Positive long requestingUserId,
            @QueryParam("oldestAdditionTimestamp") String oldestAdditionTimestamp,
            @QueryParam("oldestAdditionId") Integer oldestAdditionId,
            @PathParam("chatroomId") @Positive int chatroomId) {

        Instant parsedTimestamp = oldestAdditionTimestamp != null
                ? Instant.parse(oldestAdditionTimestamp)
                : null;

        return chatroomUserService.getUsers(
                requestingUserId,
                chatroomId,
                parsedTimestamp,
                oldestAdditionId
        );
    }

    @POST
    @Path("/{chatroomId}/users")
    @Override
    public Response addUsers(
            @QueryParam("userId") @Positive long requestingUserId,
            @PathParam("chatroomId") @Positive int chatroomId,
            @Valid AddUsersRequest request) {

        if (request == null || request.usersWithRoles() == null) {
            throw new BadRequestException("Invalid request");
        }

        Map<Long, ChatroomRole> userToRole;

        try {
            userToRole = request.usersWithRoles()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e ->
                                    ChatroomRole.valueOf(e.getValue()
                                            .toUpperCase())
                    ));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role value");
        }

        ChatroomUsersSaveData saveData =
                new ChatroomUsersSaveData(chatroomId, userToRole);

        int result = chatroomUserService.addUsers(requestingUserId, saveData);

        return Response.status(Response.Status.CREATED)
                .entity(result)
                .build();
    }

    @PUT
    @Path("/{chatroomId}/users/{affectedUserId}/role")
    @Override
    public Response changeUserRole(
            @QueryParam("userId") @Positive long requestingUserId,
            @PathParam("chatroomId") @Positive int chatroomId,
            @PathParam("affectedUserId") @Positive long affectedUserId,
            @Valid UpdateRoleRequest request) {

        if (request == null || request.updatedRole() == null) {
            throw new BadRequestException("Invalid request");
        }

        try {
            ChatroomRole.valueOf(request.updatedRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role value");
        }

        int result = chatroomUserService.changeUserRole(
                requestingUserId,
                chatroomId,
                affectedUserId,
                request.updatedRole()
        );

        return Response.ok(result).build();
    }

    @PUT
    @Path("/{chatroomId}/users/membership-status")
    @Override
    public Response changeUserMembershipStatus(
            @QueryParam("userId") @Positive long requestingUserId,
            @QueryParam("affectedUserId") @Positive long affectedUserId,
            @PathParam("chatroomId") @Positive int chatroomId,
            @Valid UpdateMembershipStatusRequest request) {

        if (request == null || request.updatedStatus() == null) {
            throw new BadRequestException("Invalid request");
        }

        try {
            MembershipStatus.valueOf(request.updatedStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid membership status");
        }

        int result = chatroomUserService.changeUserMembershipStatus(
                requestingUserId,
                chatroomId,
                affectedUserId,
                request.updatedStatus()
        );

        return Response.ok(result).build();
    }

    @PUT
    @Path("/{chatroomId}/users/update-last-read")
    public Response updateLastRead(
            @PathParam("chatroomId") @Positive int chatroomId,
            @QueryParam("userId") @Positive long userId,
            @Valid UpdateLastReadStateRequest request) {

        if (request == null) {
            throw new BadRequestException("Invalid request");
        }

        int result = chatroomUserService.updateLastReadStatus(
                userId,
                chatroomId,
                request.newLastReadState()
        );

        return Response.ok(result).build();
    }
}