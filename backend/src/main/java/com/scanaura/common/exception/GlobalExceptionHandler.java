package com.scanaura.common.exception;

import com.scanaura.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(
            CustomException ex) {

        ApiResponse<Object> response =
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        ApiResponse<Object> response =
                new ApiResponse<>(
                        false,
                        ex.getMessage(),
                        null
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        String error = ex.getBindingResult()
                .getFieldError() != null
                ? ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage()
                : "Validation failed.";

        return ResponseEntity
                .badRequest()
                .body(
                        new ApiResponse<>(
                                false,
                                error,
                                null
                        )
                );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex) {

        String message = ex.getMessage();

        ApiResponse<Object> response =
                new ApiResponse<>(
                        false,
                        message,
                        null
                );

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if ("Business not found.".equalsIgnoreCase(message)) {

            status = HttpStatus.NOT_FOUND;

        } else if ("Subscription not found.".equalsIgnoreCase(message)) {

            status = HttpStatus.NOT_FOUND;

        } else if ("Your subscription has expired. Please renew your plan."
                .equalsIgnoreCase(message)) {

            status = HttpStatus.FORBIDDEN;

        } else if ("This business is currently unavailable. Please try again later."
                .equalsIgnoreCase(message)) {

            status = HttpStatus.FORBIDDEN;

        }

        return new ResponseEntity<>(
                response,
                status
        );
    }
}