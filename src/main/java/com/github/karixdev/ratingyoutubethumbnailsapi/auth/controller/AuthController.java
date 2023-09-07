package com.github.karixdev.ratingyoutubethumbnailsapi.auth.controller;

import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.LoginRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.RegisterRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.response.LoginResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.service.AuthService;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ErrorDetails;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ValidationErrorDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @Operation(summary = "Register new account")
    @ApiResponse(
            responseCode = "204",
            description = "Successfully registered a new account"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorDetails.class)
                    )
            }
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )
            }
    )
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        service.register(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Login and get JWT")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully logged in and created JWT",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequest.class)
            )
    )
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return new ResponseEntity<>(service.login(request), HttpStatus.OK);
    }
}
