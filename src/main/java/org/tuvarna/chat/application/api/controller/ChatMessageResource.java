package org.tuvarna.chat.application.api.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.time.Instant;

@ApplicationScoped
@Path("/chat-messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ChatMessageResource {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageResource.class);

    public record MessageUpdateRequest(
            long requesterUserId,
            int chatroomId,
            ChatMessageElement message,
            String newMessage
    ) {}

    public record MessageRemoveRequest(
            long requesterUserId,
            int chatroomId,
            ChatMessageElement message
    ) {}

    @Inject
    ChatMessageService chatMessageService;

    // save is in Processor

    @PUT
    @Path("/update")
    public Response updateMessage(MessageUpdateRequest actionRequest) { // req-resp ok
        try {
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

        } catch (UserNotAllowedException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Path("/archive")
    public Response archiveMessage(MessageRemoveRequest actionRequest) { // req-resp ok
        try {
            int result = chatMessageService.archiveMessage(
                    actionRequest.requesterUserId(),
                    actionRequest.message(),
                    actionRequest.chatroomId()
            );

            if (result == 1) {
                return Response.ok().build();
            }

            return Response.status(Response.Status.BAD_REQUEST).build();

        } catch (UserNotAllowedException e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("/page")
    public ContentPage<ChatMessageElement> getMessagePage(@QueryParam("requesterUserId") long requesterUserId, // data retrieval, req-req-resp not ok
                                   @QueryParam("chatroomId") int chatroomId,
                                   @QueryParam("oldestTimestamp") String oldestTimestamp,
                                   @QueryParam("oldestId") Integer oldestId) {
        try {
            return chatMessageService.getMessagePage(
                            requesterUserId,
                            chatroomId,
                            Instant.parse(oldestTimestamp),
                            oldestId);

        } catch (UserNotAllowedException e) {
            throw new ForbiddenException(e.getMessage());

        } catch (PaginationException e) {
            throw new BadRequestException(e.getMessage());

        }
    }
}
