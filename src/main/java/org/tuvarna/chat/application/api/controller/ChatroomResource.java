package org.tuvarna.chat.application.api.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.tuvarna.chat.model.read.dto.ChatroomEventfulElement;
import org.tuvarna.chat.model.read.dto.ChatroomOverview;
import org.tuvarna.chat.model.read.dto.ContentPage;

import java.util.List;

public interface ChatroomResource {
    @POST
    @Path("/create")
    Integer createChatroom(
            @Valid @NotNull CreateChatroomRequest request);

    @PUT
    @Path("/{chatroomId}/archive")
    Response archiveChatroom(
            @QueryParam("userId") @Positive long requestingUserId,
            @PathParam("chatroomId") @Positive int chatroomId);

    @GET
    @Path("/{chatroomId}")
    ChatroomOverview getChatroomOverview(
            @QueryParam("userId") @Positive long requestingUserId,
            @PathParam("chatroomId") @Positive int chatroomId);

    @PUT
    @Path("/{chatroomId}/name")
    Response updateChatroomName(
            @PathParam("chatroomId") @Positive int chatroomId,
            @QueryParam("userId") @Positive long userId,
            @QueryParam("newName") @NotBlank String newName);

    @GET
    @Path("/events")
    ContentPage<ChatroomEventfulElement> getChatroomEventfulElements(
            @QueryParam("userId") @Positive long requestingUserId,
            @QueryParam("latestEventTimeOnPage") String latestEventTimeOnPage,
            @QueryParam("latestChatroomIdOnPage") Integer latestChatroomIdOnPage,
            @QueryParam("latestChatMessageIdOnPage") Long latestChatMessageIdOnPage);

    @GET
    @Path("/user")
    List<Integer> getChatroomIdsForUser(
            @QueryParam("userId") @Positive long requestingUserId);


    record CreateChatroomRequest(

            @NotNull
            @Positive
            Long requestingUserId,

            @NotBlank
            @Size(min = 1, max = 255)
            String name
    ) {
    }
}
