package org.tuvarna.chat.application.api.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import org.tuvarna.chat.application.api.service.ChatroomService;
import org.tuvarna.chat.application.api.service.ChatroomUserService;
import org.tuvarna.chat.model.entity.postgres.enums.ChatroomRole;
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
public class ChatroomUserResource {

    public record AddUsersRequest(Map<Long, String> usersWithRoles) {}

    public record UpdateRoleRequest(String updatedRole) {}

    public record UpdateMembershipStatusRequest(String updatedStatus) {}

    @Inject
    ChatroomUserService chatroomUserService;

    @GET
    @Path("/{chatroomId}/users")
    public Response getUsers(
            @QueryParam("userId") long requestingUserId,
            @QueryParam("oldestAdditionTimestamp") String oldestAdditionTimestamp,
            @QueryParam("oldestAdditionId") Integer oldestAdditionId,
            @PathParam("chatroomId") int chatroomId) {

        Instant parsedTimestamp = oldestAdditionTimestamp != null
                ? Instant.parse(oldestAdditionTimestamp)
                : null;

        ContentPage<ChatroomUserDetails> page =
                chatroomUserService.getUsers(
                        requestingUserId,
                        chatroomId,
                        parsedTimestamp,
                        oldestAdditionId
                );

        return Response.ok(page).build();
    }

    @POST
    @Path("/{chatroomId}/users")
    public Response addUsers(
            @QueryParam("userId") long requestingUserId,
            @PathParam("chatroomId") int chatroomId,
            AddUsersRequest request) {

        Map<Long, ChatroomRole> userToRole;

        try {
            userToRole = request.usersWithRoles().entrySet()
                    .stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> ChatroomRole.valueOf(
                                    e.getValue())));
        } catch (EnumConstantNotPresentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ChatroomUsersSaveData saveData =
                new ChatroomUsersSaveData(
                        chatroomId,
                        userToRole
                );

        int result = chatroomUserService.addUsers(requestingUserId, saveData);

        return Response.status(Response.Status.CREATED)
                .entity(result)
                .build();
    }

    @PUT
    @Path("/{chatroomId}/users/{affectedUserId}/role")
    public Response changeUserRole(
            @QueryParam("userId") long requestingUserId,
            @PathParam("chatroomId") int chatroomId,
            @PathParam("affectedUserId") long affectedUserId,
            UpdateRoleRequest request) {

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
    public Response changeUserMembershipStatus(
            @QueryParam("userId") long requestingUserId,
            @QueryParam("affectedUserId") long affectedUserId,
            @PathParam("chatroomId") int chatroomId,
            UpdateMembershipStatusRequest request) {

        int result = chatroomUserService.changeUserMembershipStatus(
                requestingUserId,
                chatroomId,
                affectedUserId,
                request.updatedStatus()
        );

        return Response.ok(result).build();
    }
}