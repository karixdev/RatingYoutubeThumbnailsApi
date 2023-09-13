package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler;

import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ValidationErrorDetails;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ErrorDetails;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Clock clock;

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorDetails> handleGlobalException(Exception e) {
        log.error("Exception occurred {}", e.getMessage(), e);

        return new ResponseEntity<>(
                new ErrorDetails(
                        LocalDateTime.now(clock),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ErrorDetails> handleAppException(AppException e) {
        log.error("Exception occurred {}", e.getMessage(), e);

        return new ResponseEntity<>(
                new ErrorDetails(
                        LocalDateTime.now(clock),
                        e.getHttpStatus().value(),
                        e.getMessage()
                ),
                e.getHttpStatus()
        );
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ValidationErrorDetails> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(
                new ValidationErrorDetails(
                        LocalDateTime.now(clock),
                        ex.getHttpStatus().value(),
                        ex.getMessage(),
                        Map.of(ex.getField(), ex.getDescription())
                ),
                ex.getHttpStatus()
        );
    }
}