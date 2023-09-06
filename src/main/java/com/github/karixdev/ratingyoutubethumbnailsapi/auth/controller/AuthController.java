package com.github.karixdev.ratingyoutubethumbnailsapi.auth.controller;

import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.RegisterRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.service.AuthService;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ErrorDetail;
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
    private final AuthService authService;

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
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            }
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetail.class)
                    )
            }
    )
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
