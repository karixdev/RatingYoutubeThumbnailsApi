package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.game.exception.GameHasNotEndedException;
import com.github.karixdev.youtubethumbnailranking.game.payload.response.GameResponse;
import com.github.karixdev.youtubethumbnailranking.rating.RatingService;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final ThumbnailService thumbnailService;
    private final RatingService ratingService;
    private final GameRepository repository;
    private final Clock clock;
    private final GameProperties properties;

    public GameResponse start(UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();

        List<Game> userGames =
                repository.findByUserOrderByLastActivityDesc(user);

        LocalDateTime now = LocalDateTime.now(clock);

        if (!userGames.isEmpty()) {
            LocalDateTime expectedEnd = userGames.get(0)
                    .getLastActivity()
                    .plusMinutes(properties.getDuration());

            if (now.isBefore(expectedEnd)) {
                throw new GameHasNotEndedException();
            }
        }

        Thumbnail thumbnail1 = thumbnailService.getRandomThumbnail();
        Thumbnail thumbnail2 = ratingService.pickOpponent(thumbnail1, user);

        Game game = repository.save(Game.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .user(user)
                .lastActivity(now)
                .build());

        return new GameResponse(game);
    }
}
