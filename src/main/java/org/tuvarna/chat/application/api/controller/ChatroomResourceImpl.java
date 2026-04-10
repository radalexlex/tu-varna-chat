package org.tuvarna.chat.application.api.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.api.service.ChatroomService;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
@Path("/chatrooms")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatroomResourceImpl implements ChatroomResource {

    private static final Logger log = LoggerFactory.getLogger(ChatroomResourceImpl.class);
    @Inject
    ChatroomService chatroomService;

    @POST
    @Path("/create")
    @Override
    public Integer createChatroom(
            @Valid CreateChatroomRequest request) {

        if (request == null || request.name() == null || request.name().isBlank()) {
            throw new BadRequestException("Invalid request");
        }

        return chatroomService.createChatroom(request.requestingUserId(), request.name());

    }

    @PUT
    @Path("/{chatroomId}/archive")
    @Override
    public Response archiveChatroom(
            @QueryParam("userId") @Positive long requestingUserId,
            @PathParam("chatroomId") @Positive int chatroomId) {

        int result = chatroomService.archiveChatroom(requestingUserId, chatroomId);

        return Response.ok(result).build();
    }

    @GET
    @Path("/{chatroomId}")
    @Override
    public ChatroomOverview getChatroomOverview(
            @QueryParam("userId") @Positive long requestingUserId,
            @PathParam("chatroomId") @Positive int chatroomId) {

        return chatroomService.getChatroomOverview(requestingUserId, chatroomId);

    }

    @GET
    @Path("/events")
    @Override
    public ContentPage<ChatroomEventfulElement> getChatroomEventfulElements(
            @QueryParam("userId") @Positive long requestingUserId,
            @QueryParam("latestEventTimeOnPage") String latestEventTimeOnPage,
            @QueryParam("latestChatroomIdOnPage") Integer latestChatroomIdOnPage,
            @QueryParam("latestChatMessageIdOnPage") Long latestChatMessageIdOnPage) {

        Instant parsedTimestamp = latestEventTimeOnPage != null
                ? Instant.parse(latestEventTimeOnPage)
                : null;

        return chatroomService.getChatroomEventfulElements(
                requestingUserId,
                parsedTimestamp,
                latestChatroomIdOnPage,
                latestChatMessageIdOnPage
        );

    }

    @GET
    @Path("/user")
    @Override
    public List<Integer> getChatroomIdsForUser(
            @QueryParam("userId")
            @Positive long requestingUserId) {

        return chatroomService.getChatroomIdsForUser(requestingUserId);

    }
}

//    @GET
//    @Path("/events/{chatroom-id}")
//    public ChatroomEventfulElement getChatroomEventfulElementById(
//            @PathParam("chatroom-id") int chatroomId,
//            @QueryParam("userId") long requestingUserId) {
//
//        try {
//            return chatroomService.getChatroomEventfulElementBy(
//                    requestingUserId,
//                    parsedTimestamp,
//                    latestChatroomIdOnPage,
//                    latestChatMessageIdOnPage
//            );
//
//        } catch (PaginationException e) {
//            log.error(e.getMessage());
//            throw new BadRequestException(e.getMessage());
//        }
//    }
