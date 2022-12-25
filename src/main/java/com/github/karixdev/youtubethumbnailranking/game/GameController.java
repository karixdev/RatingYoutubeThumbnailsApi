package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.security.CurrentUser;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService service;

    @PostMapping("/start")
    public ResponseEntity<?> start(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        return new ResponseEntity<>(
                service.start(userPrincipal),
                HttpStatus.OK
        );
    }
}
