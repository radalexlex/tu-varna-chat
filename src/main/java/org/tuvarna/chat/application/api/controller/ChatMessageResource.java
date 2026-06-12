package org.tuvarna.chat.application.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.tuvarna.chat.model.read.dto.ChatMessageElement;
import org.tuvarna.chat.model.read.dto.ContentPage;

public interface ChatMessageResource {
    @PUT
    @Path("/update")
    Response updateMessage(@Valid @NotNull MessageUpdateRequest actionRequest);

    @PUT
    @Path("/archive")
    Response archiveMessage(@Valid @NotNull MessageRemoveRequest actionRequest);

    @GET
    @Path("/page")
    ContentPage<ChatMessageElement> getMessagePage(
            @QueryParam("requesterUserId") @Positive long requesterUserId,
            @QueryParam("chatroomId") @Positive int chatroomId,
            @QueryParam("messageCursorId") Long messageCursorId,
            @QueryParam("downScroll") boolean downScroll,
            @QueryParam("initialRequest") boolean initialRequest);

    record MessageUpdateRequest(

            @Positive
            long requesterUserId,

            @Positive
            int chatroomId,

            @NotNull
            ChatMessageElement message,

            @NotBlank
            @Size(max = 5000)
            String newMessage
    ) {
    }

    record MessageRemoveRequest(

            @Positive
            long requesterUserId,

            @Positive
            int chatroomId,

            @NotNull
            ChatMessageElement message
    ) {
    }
}
