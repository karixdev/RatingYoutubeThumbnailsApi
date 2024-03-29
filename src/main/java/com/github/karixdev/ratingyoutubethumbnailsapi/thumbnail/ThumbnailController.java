package com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail;

import com.github.karixdev.ratingyoutubethumbnailsapi.security.CurrentUser;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.request.ThumbnailRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.response.ThumbnailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/thumbnail")
@RequiredArgsConstructor
public class ThumbnailController {
    private final ThumbnailService service;

    @PostMapping
    public ResponseEntity<ThumbnailResponse> add(
            @Valid @RequestBody ThumbnailRequest payload,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return new ResponseEntity<>(
                service.add(payload, userPrincipal),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> delete(
            @PathVariable(name = "id") Long id,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return new ResponseEntity<>(
                service.delete(id, userPrincipal),
                HttpStatus.OK
        );
    }
}
