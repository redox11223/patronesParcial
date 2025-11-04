package com.parcial.test.exceptions.handler;

import com.parcial.test.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

public abstract class AbstractExceptionHandler implements ExceptionHandler {
    protected ExceptionHandler next;

    @Override
    public void setNext(ExceptionHandler handler) {
        this.next = handler;
    }

    @Override
    public ErrorResponse handle(Exception exception, HttpServletRequest request) {
        if (canHandle(exception)) {
            return doHandle(exception, request);
        }

        if (next != null) {
            return next.handle(exception, request);
        }

        return buildDefaultErrorResponse(exception, request);
    }

    protected abstract ErrorResponse doHandle(Exception exception, HttpServletRequest request);

    protected ErrorResponse buildDefaultErrorResponse(Exception exception, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(500)
                .error("Internal Server Error")
                .errorCode("INTERNAL_ERROR")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}

