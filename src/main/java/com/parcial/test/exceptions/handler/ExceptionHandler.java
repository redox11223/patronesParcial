package com.parcial.test.exceptions.handler;

import com.parcial.test.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface ExceptionHandler {
    boolean canHandle(Exception exception);
    ErrorResponse handle(Exception exception, HttpServletRequest request);
    void setNext(ExceptionHandler handler);
}

