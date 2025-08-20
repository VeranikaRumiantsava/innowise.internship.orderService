package org.innowise.internship.orderservice.controllers;

import jakarta.validation.ConstraintViolationException;
import org.innowise.internship.orderservice.dto.errors.ErrorResponse;
import org.innowise.internship.orderservice.exceptions.ItemNotFoundException;
import org.innowise.internship.orderservice.exceptions.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ErrorResponse> buildErrorResponse(List<String> messages, HttpStatus status, String error) {
        ErrorResponse errorResponse = new ErrorResponse(messages, status.value(), error);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler({OrderNotFoundException.class, ItemNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(RuntimeException ex) {

        return buildErrorResponse(List.of(
                        ex.getMessage()),
                HttpStatus.NOT_FOUND,
                "Not found");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();


        return buildErrorResponse(
                errors,
                HttpStatus.BAD_REQUEST,
                "Bad request"
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(fieldError -> fieldError.getPropertyPath() + ": " + fieldError.getMessage())
                .toList();

        return buildErrorResponse(
                errors,
                HttpStatus.BAD_REQUEST,
                "Bad request"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        return buildErrorResponse(
                List.of("Invalid path variable: " + ex.getName()),
                HttpStatus.BAD_REQUEST,
                "Bad request"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(
                List.of("It's other user's order"),
                HttpStatus.UNAUTHORIZED,
                "Unauthorized"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedExceptions(Exception ex) {

        return buildErrorResponse(
                List.of("Unexpected error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error"
        );
    }
}
