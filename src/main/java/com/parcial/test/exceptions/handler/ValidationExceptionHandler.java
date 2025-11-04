package com.parcial.test.exceptions.handler;

import com.parcial.test.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidationExceptionHandler extends AbstractExceptionHandler {

    @Override
    public boolean canHandle(Exception exception) {
        return exception instanceof MethodArgumentNotValidException;
    }

    @Override
    protected ErrorResponse doHandle(Exception exception, HttpServletRequest request) {
        MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) exception;

        List<ErrorResponse.ValidationError> validationErrors = validationException
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorResponse.ValidationError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        return ErrorResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(400)
                .error("Bad Request")
                .errorCode("VALIDATION_ERROR")
                .message("Error de validación en los datos proporcionados")
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();
    }
}

