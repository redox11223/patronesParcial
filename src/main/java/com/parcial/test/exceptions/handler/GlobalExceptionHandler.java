package com.parcial.test.exceptions.handler;

import com.parcial.test.exceptions.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controlador global de excepciones - Patrón Singleton (gestionado por Spring)
 * Implementa Chain of Responsibility para el manejo de excepciones
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final BaseExceptionHandler baseExceptionHandler;
    private final ValidationExceptionHandler validationExceptionHandler;
    private final IllegalArgumentExceptionHandler illegalArgumentExceptionHandler;

    private com.parcial.test.exceptions.handler.ExceptionHandler chain;

    // Inicializar la cadena de handlers
    private void initializeChain() {
        if (chain == null) {
            // Construir la cadena de responsabilidad
            baseExceptionHandler.setNext(validationExceptionHandler);
            validationExceptionHandler.setNext(illegalArgumentExceptionHandler);
            chain = baseExceptionHandler;
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception,
            HttpServletRequest request) {

        log.error("Excepción capturada: {}", exception.getMessage(), exception);

        initializeChain();
        ErrorResponse errorResponse = chain.handle(exception, request);

        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }
}

