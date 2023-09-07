package com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @JsonProperty("email")
        @NotBlank
        @Email
        String email,
        @JsonProperty("password")
        @NotBlank
        String password
) {}
