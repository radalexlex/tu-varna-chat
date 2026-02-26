package org.tuvarna.chat.application.api.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tuvarna.chat.application.api.service.ChatroomService;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
@Path("/chatrooms")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatroomResource {

    @Inject
    ChatroomService chatroomService;

    public record CreateChatroomRequest(long requestingUserId, String name) {}

    @POST
    @Path("/create")
    public Response createChatroom(
            CreateChatroomRequest request) {

        if (request == null || request.name() == null || request.name().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        int createdChatroomUserId =
                chatroomService.createChatroom(request.requestingUserId, request.name());

        return Response.status(Response.Status.CREATED)
                .entity(createdChatroomUserId)
                .build();
    }

    @PUT
    @Path("/{chatroomId}/archive")
    public Response archiveChatroom(
            @QueryParam("userId") long requestingUserId,
            @PathParam("chatroomId") int chatroomId) {

        int result = chatroomService.archiveChatroom(requestingUserId, chatroomId);

        return Response.ok(result).build();
    }

    @GET
    @Path("/{chatroomId}")
    public Response getChatroomOverview(
            @QueryParam("userId") long requestingUserId,
            @PathParam("chatroomId") int chatroomId) {

        ChatroomOverview overview =
                chatroomService.getChatroomOverview(requestingUserId, chatroomId);

        return Response.ok(overview).build();
    }

    @GET
    @Path("/events")
    public Response getChatroomEventfulElements(
            @QueryParam("userId") long requestingUserId,
            @QueryParam("latestEventTimeOnPage") String latestEventTimeOnPage,
            @QueryParam("latestChatroomIdOnPage") Integer latestChatroomIdOnPage,
            @QueryParam("latestChatMessageIdOnPage") Long latestChatMessageIdOnPage) {

        Instant parsedTimestamp = latestEventTimeOnPage != null
                ? Instant.parse(latestEventTimeOnPage)
                : null;

        try {
            List<ChatroomEventfulElement> result =
                    chatroomService.getChatroomEventfulElements(
                            requestingUserId,
                            parsedTimestamp,
                            latestChatroomIdOnPage,
                            latestChatMessageIdOnPage
                    );

            return Response.ok(result).build();

        } catch (PaginationException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}