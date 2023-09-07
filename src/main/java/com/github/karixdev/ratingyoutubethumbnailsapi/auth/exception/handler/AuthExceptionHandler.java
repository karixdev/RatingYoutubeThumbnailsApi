package com.github.karixdev.ratingyoutubethumbnailsapi.auth.exception.handler;

import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ErrorDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {
    private final Clock clock;

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ErrorDetails> handleAuthException(AuthenticationException ex) {
        log.error("Auth error occurred", ex);

        return new ResponseEntity<>(
                new ErrorDetails(
                        LocalDateTime.now(clock),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Authentication failed"
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorDetails> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(
                new ErrorDetails(
                        LocalDateTime.now(clock),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Invalid credentials"
                ),
                HttpStatus.UNAUTHORIZED
        );
    }
}
