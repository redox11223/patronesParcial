package com.parcial.test.exceptions.handler;

import com.parcial.test.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class IllegalArgumentExceptionHandler extends AbstractExceptionHandler {

    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof IllegalArgumentException;
    }

    @Override
    protected ErrorResponse doHandle(Exception exception, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(400)
                .error("Bad Request")
                .errorCode("INVALID_ARGUMENT")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}

