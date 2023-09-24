package com.example.trafileatestproject.controller;

import com.example.trafileatestproject.exceptions.EntityNotFoundException;
import com.example.trafileatestproject.exceptions.ValidationException;
import com.example.trafileatestproject.model.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandlerController {
    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(ValidationException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityError(EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getCode(), exception.getMessage()));
    }
}
