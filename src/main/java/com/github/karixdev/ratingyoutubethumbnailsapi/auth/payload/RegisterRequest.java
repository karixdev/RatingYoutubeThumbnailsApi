package com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @JsonProperty("email")
        @NotEmpty
        @Email
        String email,
        @JsonProperty("username")
        @NotEmpty
        @Size(min = 6, max = 20)
        String username,
        @JsonProperty("password")
        @NotEmpty
        @Size(min = 6)
        String password
) {}
