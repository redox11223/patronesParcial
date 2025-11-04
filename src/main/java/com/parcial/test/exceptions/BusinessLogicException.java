package com.parcial.test.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessLogicException extends BaseException {
    public BusinessLogicException(String message) {
        super(message, "BUSINESS_LOGIC_ERROR", HttpStatus.BAD_REQUEST);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, "BUSINESS_LOGIC_ERROR", HttpStatus.BAD_REQUEST, cause);
    }
}

