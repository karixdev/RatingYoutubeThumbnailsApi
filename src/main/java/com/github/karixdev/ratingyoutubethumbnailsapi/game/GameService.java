package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasAlreadyEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.InvalidWinnerIdException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request.GameResultRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.response.GameResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingService;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.RoundService;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.PermissionDeniedException;
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
    private final RoundService roundService;


    @Transactional
    public GameResponse play(UserPrincipal userPrincipal, GameResultRequest payload) {
        User user = userPrincipal.getUser();

        List<Game> userGames = repository.findByUserOrderByLastActivityDesc(user);

        if (userGames.isEmpty() || isGameExpired(userGames.get(0))) {
            return startNewGame(user);
        }

        if (payload == null) {
            return new GameResponse(userGames.get(0));
        }

        return continueGame(userGames.get(0), user, payload);
    }

    private boolean isGameExpired(Game game) {
        LocalDateTime now = LocalDateTime.now(clock);
        return game.getHasEnded() || now.isAfter(game.getLastActivity().plusMinutes(properties.getDuration()));
    }

    private GameResponse startNewGame(User user) {
        Thumbnail thumbnail1 = thumbnailService.getRandomThumbnail();

        Game game = Game.builder()
                .thumbnail1(thumbnail1)
                .user(user)
                .lastActivity(LocalDateTime.now(clock))
                .build();

        Thumbnail thumbnail2 = ratingService.pickOpponent(game, thumbnail1, user);
        game.setThumbnail2(thumbnail2);

        repository.save(game);
        roundService.create(game, thumbnail1, thumbnail2);

        return new GameResponse(game);
    }

    private GameResponse continueGame(Game game, User user, GameResultRequest payload) {
        Long winnerId = payload.getWinnerId();

        if (!winnerId.equals(game.getThumbnail1().getId()) &&
                !winnerId.equals(game.getThumbnail2().getId())) {
            throw new InvalidWinnerIdException();
        }

        Thumbnail winner = game.getThumbnail1().getId().equals(winnerId)
                ? game.getThumbnail1() : game.getThumbnail2();

        Thumbnail loser = game.getThumbnail1().getId().equals(winnerId)
                ? game.getThumbnail2() : game.getThumbnail1();

        ratingService.updateRatings(winner, loser, user);

        Thumbnail newOpponent = ratingService.pickOpponent(game, winner, user);

        if (game.getThumbnail2().getId().equals(loser.getId())) {
            game.setThumbnail2(newOpponent);
        } else {
            game.setThumbnail1(newOpponent);
        }

        game.setLastActivity(LocalDateTime.now(clock));

        repository.save(game);
        roundService.create(game, game.getThumbnail1(), game.getThumbnail2());

        return new GameResponse(game);
    }

    @Transactional
    public SuccessResponse end(Long gameId, UserPrincipal userPrincipal) {
        Game game = getById(gameId);

        User user = userPrincipal.getUser();

        if (!game.getUser().equals(user)) {
            throw new PermissionDeniedException("You are not the owner of the game");
        }

        if (game.getHasEnded()) {
            throw new GameHasAlreadyEndedException();
        }

        game.setHasEnded(Boolean.TRUE);
        repository.save(game);

        return new SuccessResponse();
    }

    private Game getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    throw new ResourceNotFoundException(
                            "Game with provided id was not found");
                });
    }
}
