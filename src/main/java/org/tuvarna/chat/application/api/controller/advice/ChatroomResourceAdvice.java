package org.tuvarna.chat.application.api.controller.advice;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.exceptions.conflict.room.ChatroomAlreadyDeletedException;
import org.tuvarna.chat.application.exceptions.notfound.ChatroomNotFoundException;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.persistence.missing.ChatroomMissingException;
import org.tuvarna.chat.application.exceptions.service.ChatroomServiceException;
import org.tuvarna.chat.application.exceptions.validation.room.InvalidChatroomIdException;
import org.tuvarna.chat.application.exceptions.validation.room.InvalidChatroomNameException;
import org.tuvarna.chat.application.exceptions.validation.user.InvalidUserMembershipStatusException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;

@Provider
@Priority(0)
public class ChatroomResourceAdvice implements ExceptionMapper<ChatroomServiceException> {

    private static final Logger log =
            LoggerFactory.getLogger(ChatroomResourceAdvice.class);

    @Override
    public Response toResponse(ChatroomServiceException exception) {

        Throwable cause = exception.getCause();

        String msg = (cause != null && cause.getMessage() != null)
                ? cause.getMessage()
                : exception.getMessage();
        if(cause != null) {
            log.error("Handled service exception: {}", msg, cause);
        } else {
            log.error("Handled service exception: {}", msg);
        }

        if (cause instanceof UserNotAllowedException
                || cause instanceof InvalidUserMembershipStatusException) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity(msg)
                    .build();
        }

        if (cause instanceof InvalidChatroomIdException
                || cause instanceof InvalidChatroomNameException
                || cause instanceof PaginationException) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }

        if (cause instanceof ChatroomMissingException
                || cause instanceof ChatroomNotFoundException) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(msg)
                    .build();
        }

        if (cause instanceof ChatroomAlreadyDeletedException) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(msg)
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Unexpected service error")
                .build();
    }
}