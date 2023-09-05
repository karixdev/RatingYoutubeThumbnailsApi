package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.AppException;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ErrorDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Clock clock;

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorDetail> handleGlobalException(Exception e) {
        log.error("Exception occurred {}", e.getMessage(), e);

        return new ResponseEntity<>(
                new ErrorDetail(
                        LocalDateTime.now(clock),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ErrorDetail> handleAppException(AppException e) {
        log.error("Exception occurred {}", e.getMessage(), e);

        return new ResponseEntity<>(
                new ErrorDetail(
                        LocalDateTime.now(clock),
                        e.getHttpStatus(),
                        e.getMessage()
                ),
                e.getHttpStatus()
        );
    }
}
