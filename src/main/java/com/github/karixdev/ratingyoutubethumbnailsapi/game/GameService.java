package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.InvalidWinnerIdException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request.GameResultRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.response.GameResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingService;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.Round;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.ThumbnailService;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import jakarta.transaction.Transactional;
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

    @Transactional
    public GameResponse play(UserPrincipal userPrincipal, GameResultRequest payload) {
        User user = userPrincipal.getUser();

        List<Game> userGames = repository.findByUserOrderByLastActivityDesc(user);

        if (userGames.isEmpty() || userGames.get(0).isGameExpired(clock, properties.getDuration())) {
            return startNewGame(user);
        }

        if (payload == null) {
            return new GameResponse(userGames.get(0));
        }

        return continueGame(userGames.get(0), user, payload);
    }

    private GameResponse startNewGame(User user) {
        Game game = Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now(clock))
                .build();

        Thumbnail thumbnail1 = thumbnailService.getRandomThumbnail();
        Thumbnail thumbnail2 = ratingService.pickOpponent(game, thumbnail1, user);

        game.addRound(thumbnail1, thumbnail2, clock);

        repository.save(game);

        return new GameResponse(game);
    }

    private GameResponse continueGame(Game game, User user, GameResultRequest payload) {
        Long winnerId = payload.getWinnerId();
        Round latestRound = game.getLatestRound();

        if (!winnerId.equals(latestRound.getThumbnail1().getId()) &&
                !winnerId.equals(latestRound.getThumbnail2().getId())) {
            throw new InvalidWinnerIdException();
        }

        Thumbnail winner = latestRound.getThumbnail1().getId().equals(winnerId)
                ? latestRound.getThumbnail1() : latestRound.getThumbnail2();

        Thumbnail loser = latestRound.getThumbnail1().getId().equals(winnerId)
                ? latestRound.getThumbnail2() : latestRound.getThumbnail1();

        ratingService.updateRatings(winner, loser, user);

        Thumbnail newOpponent = ratingService.pickOpponent(game, winner, user);

        game.setLastActivity(LocalDateTime.now(clock));
        game.addRound(winner, newOpponent, clock);

        return new GameResponse(game);
    }

    @Transactional
    public void end(Long gameId, UserPrincipal userPrincipal) {
        Game game = getByIdOrElseThrow(gameId);
        User user = userPrincipal.getUser();

        game.endGame(user, clock, properties.getDuration());
        repository.save(game);
    }

    private Game getByIdOrElseThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game with provided id was not found"));
    }
}
