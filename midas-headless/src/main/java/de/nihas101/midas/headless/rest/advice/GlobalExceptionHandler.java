package de.nihas101.midas.headless.rest.advice;

import de.nihas101.midas.core.lock.service.LockedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(final MethodArgumentTypeMismatchException e) {
        return toParseErrorResponse(e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatchForEnums(final HttpMessageNotReadableException e) {
        return toParseErrorResponse(e);
    }

    private ResponseEntity<Map<String, String>> toParseErrorResponse(final Exception e) {
        log.trace("A type passed to the API could not be parsed", e);
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid request parameter"));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, String>> handleLockedException(final LockedException e) {
        log.debug("A locked year was attempted to be edited", e);
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(Map.of("error", "Locked", "message", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.debug("An illegal argument was passed", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad Request", "message", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(final IllegalStateException e) {
        log.debug("An illegal state occurred", e);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Conflict", "message", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(final Exception e) {
        log.error("An unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Internal Server Error"));
    }

}
