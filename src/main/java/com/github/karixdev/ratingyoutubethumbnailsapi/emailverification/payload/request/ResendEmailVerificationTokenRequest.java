package com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResendEmailVerificationTokenRequest {
    @JsonProperty("email")
    @Email
    private String email;
}
