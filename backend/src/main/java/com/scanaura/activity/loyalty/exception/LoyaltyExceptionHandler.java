package com.scanaura.activity.loyalty.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class LoyaltyExceptionHandler {

    private static final String DAILY_VISIT_INDEX =
            "ux_loyalty_daily_earn";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException exception
    ) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            IllegalStateException exception
    ) {

        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException exception
    ) {

        if (containsDailyVisitConstraint(exception)) {
            return buildResponse(
                    HttpStatus.CONFLICT,
                    "You have already earned loyalty points today."
            );
        }

        return buildResponse(
                HttpStatus.CONFLICT,
                "This request could not be completed."
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception
    ) {

        String message =
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .findFirst()
                        .map(error -> error.getDefaultMessage())
                        .orElse("Invalid request.");

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    private boolean containsDailyVisitConstraint(
            Throwable exception
    ) {

        Throwable current = exception;

        while (current != null) {

            String message = current.getMessage();

            if (message != null
                    && message.contains(DAILY_VISIT_INDEX)) {

                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String message
    ) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("success", false);

        response.put(
                "message",
                message != null && !message.isBlank()
                        ? message
                        : "Something went wrong."
        );

        response.put("data", null);

        return ResponseEntity
                .status(status)
                .body(response);
    }
}