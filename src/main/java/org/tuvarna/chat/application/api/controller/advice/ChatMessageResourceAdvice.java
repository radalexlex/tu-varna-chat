package org.tuvarna.chat.application.api.controller.advice;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuvarna.chat.application.exceptions.page.PaginationException;
import org.tuvarna.chat.application.exceptions.persistence.DataPersistenceException;
import org.tuvarna.chat.application.exceptions.persistence.missing.ChatMessageMissingException;
import org.tuvarna.chat.application.exceptions.service.ChatMessageServiceException;
import org.tuvarna.chat.application.exceptions.validation.message.EmptyMessageContentException;
import org.tuvarna.chat.application.exceptions.validation.message.MessageTooLongException;
import org.tuvarna.chat.application.exceptions.validation.room.InvalidChatroomIdException;
import org.tuvarna.chat.application.exceptions.validation.user.InvalidUserMembershipStatusException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotAllowedException;
import org.tuvarna.chat.application.exceptions.violation.user.UserNotChatroomMemberException;

@Provider
@Priority(0)
public class ChatMessageResourceAdvice implements ExceptionMapper<ChatMessageServiceException> {

    private static final Logger log = LoggerFactory.getLogger(ChatMessageResourceAdvice.class);

    @Override
    public Response toResponse(ChatMessageServiceException exception) {

        Throwable cause = exception.getCause();

        try {
            log.warn("Handled service exception: {}", cause != null ?
                    cause.getMessage()
                    : exception.getMessage());
        } catch (NullPointerException e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal server error, null exception")
                    .build();
        }

        if (cause instanceof UserNotAllowedException
                || cause instanceof UserNotChatroomMemberException
                || cause instanceof InvalidUserMembershipStatusException) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(cause.getMessage())
                    .build();
        }

        if (cause instanceof EmptyMessageContentException
                || cause instanceof MessageTooLongException
                || cause instanceof InvalidChatroomIdException
                || cause instanceof PaginationException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(cause.getMessage())
                    .build();
        }

        if (cause instanceof ChatMessageMissingException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(cause.getMessage())
                    .build();
        }

        if (cause instanceof DataPersistenceException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(cause.getMessage())
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Unexpected service error")
                .build();
    }
}
