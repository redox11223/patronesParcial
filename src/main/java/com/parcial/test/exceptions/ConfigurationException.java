package com.parcial.test.exceptions;

import org.springframework.http.HttpStatus;

public class ConfigurationException extends BaseException {
    public ConfigurationException(String message) {
        super(message, "CONFIGURATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, "CONFIGURATION_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}

