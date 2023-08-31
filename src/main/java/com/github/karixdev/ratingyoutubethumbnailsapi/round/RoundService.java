package com.github.karixdev.ratingyoutubethumbnailsapi.round;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoundService {
    private final RoundRepository repository;

    @Transactional
    public Round create(Game game, Thumbnail thumbnail1, Thumbnail thumbnail2) {
        Round round = Round.builder()
                .game(game)
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .build();

        return repository.save(round);
    }

}
