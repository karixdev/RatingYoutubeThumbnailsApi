package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request.GameResultRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.response.GameResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.CurrentUser;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService service;

    @PostMapping("/start")
    public ResponseEntity<GameResponse> start(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return new ResponseEntity<>(
                service.start(userPrincipal),
                HttpStatus.OK
        );
    }

    @PostMapping("/round-result/{id}")
    public ResponseEntity<GameResponse> result(
            @PathVariable(name = "id") Long id,
            @RequestBody GameResultRequest payload,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return new ResponseEntity<>(
                service.roundResult(id, payload, userPrincipal),
                HttpStatus.OK
        );
    }

    @PostMapping("/end/{id}")
    public ResponseEntity<SuccessResponse> end(
            @PathVariable(name = "id") Long id,
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return new ResponseEntity<>(
                service.end(id, userPrincipal),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<GameResponse> getUserActualActiveGame(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return new ResponseEntity<>(
                service.getUserActualActiveGame(userPrincipal),
                HttpStatus.OK
        );
    }
}
