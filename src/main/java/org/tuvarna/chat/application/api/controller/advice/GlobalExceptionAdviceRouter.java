package org.tuvarna.chat.application.api.controller.advice;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(2)
public class GlobalExceptionAdviceRouter implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionAdviceRouter.class);

    @Override
    public Response toResponse(Exception ex) {

        Throwable cause = ex.getCause();

        try {
            log.error(cause.getMessage(), cause);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unexpected error, retrieved state: " + cause.getMessage())
                    .build();

        } catch (NullPointerException e) {
            log.error("Unexpected error, nothing retrieved", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unexpected error, nothing retrieved.")
                    .build();
        }

//
//        if (ex instanceof org.hibernate.exception.ConstraintViolationException) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity("Constraint violation")
//                    .build();
//        }
//
//        if (ex instanceof jakarta.persistence.PersistenceException) {
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("Database error")
//                    .build();
//        }


    }

}