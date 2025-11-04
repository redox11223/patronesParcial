package com.parcial.test.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String field, String message) {
        super(
            String.format("Error de validación en el campo '%s': %s", field, message),
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST
        );
    }
}

