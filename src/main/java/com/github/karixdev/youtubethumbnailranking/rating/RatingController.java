package com.github.karixdev.youtubethumbnailranking.rating;

import com.github.karixdev.youtubethumbnailranking.rating.payload.response.RatingResponse;
import com.github.karixdev.youtubethumbnailranking.security.CurrentUser;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService service;

    @GetMapping("/{youtubeVideoId}")
    public ResponseEntity<RatingResponse> getThumbnailAveragePoints(
            @PathVariable(name = "youtubeVideoId") String youtubeVideoId,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return new ResponseEntity<>(
                service.getThumbnailAveragePoints(youtubeVideoId, userPrincipal),
                HttpStatus.OK
        );
    }
}
