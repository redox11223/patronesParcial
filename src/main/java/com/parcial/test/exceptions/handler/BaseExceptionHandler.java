package com.parcial.test.exceptions.handler;

import com.parcial.test.exceptions.BaseException;
import com.parcial.test.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class BaseExceptionHandler extends AbstractExceptionHandler {

    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof BaseException;
    }

    @Override
    protected ErrorResponse doHandle(Exception exception, HttpServletRequest request) {
        BaseException baseException = (BaseException) exception;

        return ErrorResponse.builder()
                .timestamp(baseException.getTimestamp())
                .status(baseException.getHttpStatus().value())
                .error(baseException.getHttpStatus().getReasonPhrase())
                .errorCode(baseException.getErrorCode())
                .message(baseException.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}

