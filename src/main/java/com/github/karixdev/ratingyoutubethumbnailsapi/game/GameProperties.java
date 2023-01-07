package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class GameProperties {
    private final Integer duration;

    public GameProperties(
            @Value("${game.duration}") Integer duration
    ) {
        this.duration = duration;
    }
}
