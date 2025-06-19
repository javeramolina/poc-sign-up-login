package com.jotavera.demo.auth.exception;

import com.jotavera.demo.auth.dto.response.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles global exceptions and maps them to proper HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles existing user registration attempts.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ErrorDetail error = new ErrorDetail(409, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("error", List.of(error))
        );
    }

    /**
     * Handles validation errors for invalid method arguments.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorDetail(400, err.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errors));
    }

    /**
     * Handles invalid JWT token exceptions.
     */
    @ExceptionHandler(JwtInvalidException.class)
    public ResponseEntity<?> handleJwtInvalidException(JwtInvalidException ex) {
        ErrorDetail error = new ErrorDetail(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", List.of(error))
        );
    }

    /**
     * Handles JWT signature exceptions.
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureExceptionInvalidException(SignatureException ex) {
        ErrorDetail error = new ErrorDetail(401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", List.of(error))
        );
    }

    /**
     * Handles any unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        ErrorDetail error = new ErrorDetail(500, "Unexpected error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("error", List.of(error))
        );
    }
}