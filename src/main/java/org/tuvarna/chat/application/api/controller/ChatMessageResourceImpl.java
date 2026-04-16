package org.tuvarna.chat.application.api.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.time.Instant;

@ApplicationScoped
@Path("/chat-messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatMessageResourceImpl implements ChatMessageResource {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageResourceImpl.class);

    @Inject
    ChatMessageService chatMessageService;

    @PUT
    @Path("/update")
    @Override
    public Response updateMessage(@Valid @NotNull MessageUpdateRequest actionRequest) {

        int result = chatMessageService.updateMessage(
                actionRequest.requesterUserId(),
                actionRequest.message(),
                actionRequest.chatroomId(),
                actionRequest.newMessage()
        );

        if (result == 1) {
            return Response.ok().build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("/archive")
    @Override
    public Response archiveMessage(@Valid @NotNull MessageRemoveRequest actionRequest) {

        int result = chatMessageService.archiveMessage(
                actionRequest.requesterUserId(),
                actionRequest.message(),
                actionRequest.chatroomId()
        );

        if (result == 1) {
            return Response.ok().build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/page")
    @Override
    public ContentPage<ChatMessageElement> getMessagePage(
            @QueryParam("requesterUserId") @Positive long requesterUserId,
            @QueryParam("chatroomId") @Positive int chatroomId,
            @QueryParam("oldestTimestamp") String oldestTimestamp,
            @QueryParam("oldestId") @Positive Integer oldestId,
            @QueryParam("requestForOlder") boolean requestForOlder) {

        return chatMessageService.getMessagePage(
                requesterUserId,
                chatroomId,
                oldestTimestamp != null ? Instant.parse(oldestTimestamp) : null,
                oldestId,
                requestForOlder
        );
    }
}