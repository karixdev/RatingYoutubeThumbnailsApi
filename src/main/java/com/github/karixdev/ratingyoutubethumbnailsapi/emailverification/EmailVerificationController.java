package com.github.karixdev.ratingyoutubethumbnailsapi.emailverification;

import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.payload.request.ResendEmailVerificationTokenRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService service;

    @PostMapping("/{token}")
    public ResponseEntity<SuccessResponse> verify(
            @PathVariable(name = "token") String token
    ) {
        return new ResponseEntity<>(
                service.verify(token),
                HttpStatus.OK
        );
    }

    @PostMapping("/resend")
    public ResponseEntity<SuccessResponse> resend(
            @Valid @RequestBody ResendEmailVerificationTokenRequest payload
    ) {
        return new ResponseEntity<>(
                service.resend(payload),
                HttpStatus.OK
        );
    }
}
