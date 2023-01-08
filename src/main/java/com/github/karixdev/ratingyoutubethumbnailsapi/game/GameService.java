package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasAlreadyEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasNotEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.InvalidWinnerIdException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request.GameResultRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.response.GameResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingService;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.PermissionDeniedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.ThumbnailService;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public GameResponse start(UserPrincipal userPrincipal) {
        User user = userPrincipal.getUser();

        List<Game> userGames =
                repository.findByUserOrderByLastActivityDesc(user);

        LocalDateTime now = LocalDateTime.now(clock);

        if (!userGames.isEmpty()) {
            LocalDateTime expectedEnd = userGames.get(0)
                    .getLastActivity()
                    .plusMinutes(properties.getDuration());

            if (now.isBefore(expectedEnd) && !userGames.get(0).getHasEnded()) {
                throw new GameHasNotEndedException();
            }
        }

        Thumbnail thumbnail1 = thumbnailService.getRandomThumbnail();
        Thumbnail thumbnail2 = ratingService.pickOpponent(thumbnail1, user, null);

        Game game = repository.save(Game.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .user(user)
                .lastActivity(now)
                .build());

        return new GameResponse(game);
    }

    @Transactional
    public GameResponse roundResult(Long gameId, GameResultRequest payload, UserPrincipal userPrincipal) {
        Game game = getById(gameId);

        User user = userPrincipal.getUser();

        if (!game.getUser().equals(user)) {
            throw new PermissionDeniedException("You are not the owner of the game");
        }

        LocalDateTime now = LocalDateTime.now(clock);

        if (now.isAfter(game.getLastActivity().plusMinutes(properties.getDuration())) || game.getHasEnded()) {
            throw new GameHasEndedException();
        }

        Long winnerId = payload.getWinnerId();

        if (!winnerId.equals(game.getThumbnail1().getId()) &&
                !winnerId.equals(game.getThumbnail2().getId())) {
            throw new InvalidWinnerIdException();
        }

        Thumbnail winner = game.getThumbnail1().getId().equals(winnerId)
                ? game.getThumbnail1() : game.getThumbnail2();

        Thumbnail loser = game.getThumbnail1().getId().equals(winnerId)
                ? game.getThumbnail2() : game.getThumbnail1();

        // update ratings
        ratingService.updateRatings(winner, loser, user);

        // pick new opponent
        Thumbnail newOpponent = ratingService.pickOpponent(winner, user, loser);

        if (game.getThumbnail2().getId().equals(loser.getId())) {
            game.setThumbnail2(newOpponent);
        } else {
            game.setThumbnail1(newOpponent);
        }

        // update last activity
        game.setLastActivity(now);

        game = repository.save(game);

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

    public GameResponse getUserActualActiveGame(UserPrincipal userPrincipal) {
        List<Game> userNotEndedGames =
                repository.findByUserAndHasEndedOrderByLastActivityDesc(
                        userPrincipal.getUser(), Boolean.FALSE);

        if (userNotEndedGames.isEmpty()) {
            throw new ResourceNotFoundException("There is no actual active game");
        }

        Game newestGame = userNotEndedGames.get(0);

        LocalDateTime now = LocalDateTime.now(clock);

        if (now.isAfter(newestGame.getLastActivity().plusMinutes(properties.getDuration()))) {
            throw new ResourceNotFoundException("There is no actual active game");
        }

        return new GameResponse(newestGame);
    }

    private Game getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    throw new ResourceNotFoundException(
                            "Game with provided id was not found");
                });
    }
}
