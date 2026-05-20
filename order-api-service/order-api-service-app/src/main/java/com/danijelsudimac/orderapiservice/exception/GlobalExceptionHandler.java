package com.danijelsudimac.orderapiservice.exception;

import com.danijelsudimac.orderapiservice.exception.model.ErrorResponse;
import com.danijelsudimac.orderapiservice.exception.model.FieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    private static final String VALIDATION_ERROR_MESSAGE = "Validation failed for one or more fields";
    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldError(err.getField(), err.getDefaultMessage()))
                .toList();
        log.warn(VALIDATION_ERROR_MESSAGE + ": {}", errors);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        VALIDATION_ERROR_CODE,
                        errors,
                        Instant.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        log.error(UNEXPECTED_ERROR_MESSAGE, ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(UNEXPECTED_ERROR_MESSAGE);
    }
}
