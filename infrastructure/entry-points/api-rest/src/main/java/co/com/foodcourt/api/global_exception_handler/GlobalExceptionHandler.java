package co.com.foodcourt.api.global_exception_handler;


import co.com.foodcourt.api.common.LogConstants;
import co.com.foodcourt.api.exception.UnauthorizedException;
import co.com.foodcourt.model.plate.exception.DishNotFoundException;
import co.com.foodcourt.model.user.exception.ExternalServiceException;
import co.com.foodcourt.usecase.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(LogConstants.TIMESTAMP.getMessage(), LocalDateTime.now());
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        body.put(LogConstants.ERROR.getMessage(), errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(LogConstants.TIMESTAMP.getMessage(), LocalDateTime.now());
        body.put(LogConstants.ERROR.getMessage(), "Business validation error");
        body.put(LogConstants.DETAILS.getMessage(), ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error",ex);
        Map<String, Object> body = new HashMap<>();
        body.put(LogConstants.TIMESTAMP.getMessage(), LocalDateTime.now());
        body.put(LogConstants.ERROR.getMessage(), "Unexpected error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<Map<String, Object>> handleExternalServiceException(ExternalServiceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(LogConstants.TIMESTAMP.getMessage(), LocalDateTime.now());
        body.put(LogConstants.ERROR.getMessage(), "Unexpected error");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(LogConstants.TIMESTAMP.getMessage(), LocalDateTime.now());
        body.put(LogConstants.ERROR.getMessage(), "Unauthorized");
        body.put(LogConstants.DETAILS.getMessage(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(DishNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDishNotFoundException(DishNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put(LogConstants.TIMESTAMP.getMessage(), LocalDateTime.now());
        body.put(LogConstants.ERROR.getMessage(), "Business validation error");
        body.put(LogConstants.DETAILS.getMessage(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
