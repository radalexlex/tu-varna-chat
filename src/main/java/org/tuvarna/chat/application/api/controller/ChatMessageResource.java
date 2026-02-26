package org.tuvarna.chat.application.api.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.tuvarna.chat.application.api.service.ChatMessageService;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;

@ApplicationScoped
@Path("/chat-messages")
public class ChatMessageResource {

    public record MessageUpdateRequest(long requesterUserId, ChatMessageElement message, String newMessage) {}

    public record MessageRemoveRequest(long requesterUserId, ChatMessageElement message) {}

    @Inject
    ChatMessageService chatMessageService;

    @PUT
    @Path("/update")
    public Response updateMessage(MessageUpdateRequest actionRequest) {
        if(chatMessageService.updateMessage(
                actionRequest.requesterUserId,
                actionRequest.message(),
                actionRequest.newMessage()) == 1) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/archive")
    public Response archiveMessage(MessageRemoveRequest actionRequest) {
        if(chatMessageService.archiveMessage(
                actionRequest.requesterUserId,
                actionRequest.message()) == 1) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}
