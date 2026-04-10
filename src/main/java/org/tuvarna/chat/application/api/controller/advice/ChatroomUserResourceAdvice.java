package org.tuvarna.chat.application.api.controller.advice;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.persistence.missing.ChatroomUserMissingException;
import org.tuvarna.chat.application.exceptions.service.ChatroomUserServiceException;
import org.tuvarna.chat.application.exceptions.validation.room.InvalidChatroomIdException;
import org.tuvarna.chat.application.exceptions.validation.user.InvalidUserMembershipStatusException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;

@Provider
@Priority(0)
public class ChatroomUserResourceAdvice
        implements ExceptionMapper<ChatroomUserServiceException> {

    private static final Logger log =
            LoggerFactory.getLogger(ChatroomUserResourceAdvice.class);

    @Override
    public Response toResponse(ChatroomUserServiceException exception) {

        Throwable cause = exception.getCause();

        String msg = (cause != null && cause.getMessage() != null)
                ? cause.getMessage()
                : exception.getMessage();

        log.warn("Handled service exception: {}", msg);

        if (cause instanceof UserNotAllowedException
                || cause instanceof InvalidUserMembershipStatusException) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity(msg)
                    .build();
        }

        if (cause instanceof PaginationException
                || cause instanceof InvalidChatroomIdException) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }

        if (cause instanceof ChatroomUserMissingException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(msg)
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Unexpected service error")
                .build();
    }
}
