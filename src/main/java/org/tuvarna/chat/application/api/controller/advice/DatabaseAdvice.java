package org.tuvarna.chat.application.api.controller.advice;


import jakarta.annotation.Priority;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.hibernate.StaleObjectStateException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(1)
public class DatabaseAdvice implements ExceptionMapper<PersistenceException> {

    private static final Logger log = LoggerFactory.getLogger(DatabaseAdvice.class);

    @Override
    public Response toResponse(PersistenceException exception) {

        Throwable root = exception.getCause();

        try {
            log.warn("Handled service exception: {}", root != null ?
                    root.getMessage()
                    : exception.getMessage());
        } catch (NullPointerException e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal server error, null exception")
                    .build();
        }

        if (root instanceof ConstraintViolationException
                || root instanceof EntityExistsException) {

            return Response.status(Response.Status.CONFLICT)
                    .entity("Database constraint violation: " + root.getMessage())
                    .build();
        }

        if (root instanceof DataException) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid data for database operation: " + root.getMessage())
                    .build();
        }

        if (root instanceof EntityNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Entity not found: " + root.getMessage())
                    .build();
        }

        if (root instanceof OptimisticLockException
                || root instanceof StaleObjectStateException) {

            return Response.status(Response.Status.CONFLICT)
                    .entity("Concurrent modification detected: " + root.getMessage())
                    .build();
        }

        if (root instanceof JDBCConnectionException
                || root instanceof LockAcquisitionException) {

            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("Database temporarily unavailable. Please try again later.")
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Database error: " + exception.getMessage())
                .build();
    }
}
