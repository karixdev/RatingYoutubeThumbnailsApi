package com.github.karixdev.ratingyoutubethumbnailsapi.game.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.entity.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.InsufficientNumberOfAvailableVideos;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.mapper.GameMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request.GameResultRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.repository.GameRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.game.GameDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.VideoServiceApi;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final OpponentSelectionService opponentSelectionService;
    private final GameRepository repository;
    private final GameMapper mapper;
    private final Clock clock;

    @Transactional
    public GameDTO play(UserDTO user, GameResultRequest resultRequest) {
        List<Game> userLatestGames = repository.findByUserIdOrderByLastActivityDesc(user.id());

        LocalDateTime now = LocalDateTime.now(clock);
        if (userLatestGames.isEmpty() || userLatestGames.get(0).isGameExpired(now)) {
            return startGame(user);
        }

        // continue
        return null;
    }

    public GameDTO startGame(UserDTO user) {
        Game game = Game.builder()
                .userId(user.id())
                .lastActivity(LocalDateTime.now(clock))
                .build();

        VideoDTO firstOpponent = opponentSelectionService.selectFirstOpponent(user).orElseThrow(InsufficientNumberOfAvailableVideos::new);
        VideoDTO secondOpponent = opponentSelectionService.selectSecondOpponent(user, game, firstOpponent).orElseThrow(InsufficientNumberOfAvailableVideos::new);

        game.addRound(firstOpponent, secondOpponent, clock);
        repository.save(game);

        return mapper.entityToDTO(game);
    }

}
