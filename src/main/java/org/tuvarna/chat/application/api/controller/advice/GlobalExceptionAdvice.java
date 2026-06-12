package org.tuvarna.chat.application.api.controller.advice;

import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;

@Provider
@Priority(2)
public class GlobalExceptionAdvice implements ExceptionMapper<Exception> {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @Override
    public Response toResponse(Exception ex) {

        try {
            Throwable cause = ex.getCause();

            if (ex instanceof WebApplicationException
                    || ex instanceof DateTimeException) {
                try {
                    log.error(cause.getMessage(), cause);
                } catch (NullPointerException e) {
                    log.error("Could not get message from cause, direct exception output: ", ex );
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Bad request, could not retrieve state. " +
                                    "Main exception: " + ex)
                            .build();
                }
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Bad request, retrieved state: " +
                                cause.getMessage() + " Main exception: " + ex)
                        .build();
            }
            log.error(cause.getMessage(), cause);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unexpected error, retrieved state: " +
                            cause.getMessage() + " Main exception: " + ex)
                    .build();

        } catch (NullPointerException e) {
            log.error("Unexpected error, nothing retrieved, NPE:{}, original exception: ", e, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Unexpected error, nothing retrieved. " +
                            "Main exception: " + ex + " NPE: " + e)
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