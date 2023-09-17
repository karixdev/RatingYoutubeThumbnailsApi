package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasAlreadyEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.InvalidWinnerIdException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request.GameResultRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.response.GameResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingService;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.Round;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.PermissionDeniedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.ThumbnailService;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.response.ThumbnailResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @InjectMocks
    GameService underTest;

    @Mock
    ThumbnailService thumbnailService;

    @Mock
    RatingService ratingService;

    @Mock
    GameRepository repository;

    @Mock
    Clock clock;

    @Mock
    GameProperties properties;

    User user;

    Thumbnail thumbnail1;

    Thumbnail thumbnail2;

    private static final ZonedDateTime NOW = ZonedDateTime.of(
            2022,
            11,
            23,
            13,
            44,
            30,
            0,
            ZoneId.of("UTC+1")
    );

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        thumbnail1 = Thumbnail.builder()
                .id(1L)
                .addedBy(user)
                .url("thumbnail-url-1")
                .youtubeVideoId("youtube-id-1")
                .build();

        thumbnail2 = Thumbnail.builder()
                .id(2L)
                .addedBy(user)
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .build();
    }

    @Test
    void GivenUserWithEmptyGamesSet_WhenPlay_ThenStartsNewGame() {
        // Given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(repository.findByUserOrderByLastActivityDesc(eq(user)))
                .thenReturn(List.of());

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        Game game = Game.builder()
                .user(user)
                .lastActivity(NOW.toLocalDateTime())
                .build();

        Thumbnail thumbnail1 = createThumbnail(1L, "url-1", "yt-id-1", user);
        when(thumbnailService.getRandomThumbnail())
                .thenReturn(thumbnail1);

        Thumbnail thumbnail2 = createThumbnail(1L, "url-2", "yt-id-2", user);
        when(ratingService.pickOpponent(eq(game), eq(thumbnail1), eq(user)))
                .thenReturn(thumbnail2);

        // When
        underTest.play(userPrincipal, null);

        // Then
        verify(repository).save(eq(game));
    }

    @Test
    void GivenUserWithExpiredLatestGame_WhenPlay_ThenStartsNewGame() {
        // Given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(repository.findByUserOrderByLastActivityDesc(eq(user)))
                .thenReturn(List.of(Game.builder().id(1L).hasEnded(true).build()));

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        Game game = Game.builder()
                .user(user)
                .lastActivity(NOW.toLocalDateTime())
                .build();

        Thumbnail thumbnail1 = createThumbnail(1L, "url-1", "yt-id-1", user);
        when(thumbnailService.getRandomThumbnail())
                .thenReturn(thumbnail1);

        Thumbnail thumbnail2 = createThumbnail(1L, "url-2", "yt-id-2", user);
        when(ratingService.pickOpponent(eq(game), eq(thumbnail1), eq(user)))
                .thenReturn(thumbnail2);

        // When
        underTest.play(userPrincipal, null);

        // Then
        verify(repository).save(eq(game));
    }

    @Test
    void GivenUserWithActiveGameAndNullGameResultRequest_WhenPlay_ThenCorrectGameResponseIsReturned() {
        // Given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        Thumbnail thumbnail1 = createThumbnail(1L, "url-1", "yt-id-1", user);
        Thumbnail thumbnail2 = createThumbnail(2L, "url-2", "yt-id-2", user);
        Game game = Game.builder()
                .id(1L)
                .hasEnded(false)
                .lastActivity(NOW.toLocalDateTime().minusMinutes(1))
                .build();
        game.addRound(thumbnail1, thumbnail2, clock);

        when(properties.getDuration()).thenReturn(10);

        when(repository.findByUserOrderByLastActivityDesc(eq(user)))
                .thenReturn(List.of(game));

        // When
        GameResponse result = underTest.play(userPrincipal, null);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getThumbnails().stream().map(ThumbnailResponse::getId))
                .containsExactly(thumbnail1.getId(), thumbnail2.getId());
    }

    @Test
    void GivenUserWithActiveGameAndNotNullInvalidRequest_WhenPlay_ThenInvalidWinnerIdExceptionIsThrown() {
        // Given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        GameResultRequest payload = new GameResultRequest(34L);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        Thumbnail thumbnail1 = createThumbnail(1L, "url-1", "yt-id-1", user);
        Thumbnail thumbnail2 = createThumbnail(2L, "url-2", "yt-id-2", user);
        Game game = Game.builder()
                .id(1L)
                .hasEnded(false)
                .lastActivity(NOW.toLocalDateTime().minusMinutes(1))
                .build();
        game.addRound(thumbnail1, thumbnail2, clock);

        when(properties.getDuration()).thenReturn(10);

        when(repository.findByUserOrderByLastActivityDesc(eq(user)))
                .thenReturn(List.of(game));

        // When & Then
        assertThatThrownBy(() -> underTest.play(userPrincipal, payload))
                .isInstanceOf(InvalidWinnerIdException.class);
    }

    @Test
    void GivenUserWithActiveGameAndNotNullRequest_WhenPlay_ThenRatingsAreUpdatedAndNewOpponentIsChosen() {
        // Given
        User user = createUser();
        UserPrincipal userPrincipal = new UserPrincipal(user);

        GameResultRequest payload = new GameResultRequest(1L);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        Thumbnail thumbnail1 = createThumbnail(1L, "url-1", "yt-id-1", user);
        Thumbnail thumbnail2 = createThumbnail(2L, "url-2", "yt-id-2", user);
        Game game = Game.builder()
                .id(1L)
                .hasEnded(false)
                .lastActivity(NOW.toLocalDateTime().minusMinutes(1))
                .build();

        Set<Round> rounds = new HashSet<>();
        rounds.add(createRound(game, thumbnail1, thumbnail2));
        game.setRounds(rounds);

        when(properties.getDuration()).thenReturn(10);

        when(repository.findByUserOrderByLastActivityDesc(eq(user)))
                .thenReturn(List.of(game));

        Thumbnail thumbnail3 = createThumbnail(3L, "url-3", "yt-id-3", user);
        when(ratingService.pickOpponent(eq(game), eq(thumbnail1), eq(user)))
                .thenReturn(thumbnail3);

        // When
        underTest.play(userPrincipal, payload);

        // Then
        verify(ratingService).updateRatings(
                eq(thumbnail1),
                eq(thumbnail2),
                eq(user)
        );

        Round latestRound = game.getLatestRound();
        assertThat(latestRound.getThumbnail1()).isEqualTo(thumbnail1);
        assertThat(latestRound.getThumbnail2()).isEqualTo(thumbnail3);
    }

    @Test
    void GivenNotExistingGameId_WhenEnd_ThenThrowsResourceNotFoundExceptionWithCorrectMessage() {
        // Given
        Long gameId = 1337L;
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(repository.findById(eq(gameId)))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.end(gameId, userPrincipal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Game with provided id was not found");
    }

    @Test
    void GivenValidGameIdAndValidUserPrincipal_WhenEnd_ThenEndsGame() {
        // Given
        Long gameId = 1L;
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        Game game = Game.builder()
                .id(gameId)
                .user(user)
                .lastActivity(NOW.toLocalDateTime())
                .build();

        when(repository.findById(eq(gameId)))
                .thenReturn(Optional.of(game));


        // When
        underTest.end(gameId, new UserPrincipal(user));

        // Then
        assertThat(game.getHasEnded()).isTrue();
        verify(repository).save(game);
    }

    private static Round createRound(Game game, Thumbnail thumbnail1, Thumbnail thumbnail2) {
        return Round.builder()
                .id(UUID.randomUUID())
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .game(game)
                .createdAt(NOW.toLocalDateTime().minusMinutes(5))
                .build();
    }

    private static Thumbnail createThumbnail(Long id, String url, String ytId, User user) {
        return Thumbnail.builder()
                .id(id)
                .url(url)
                .youtubeVideoId(ytId)
                .addedBy(user)
                .build();
    }

    private static User createUser() {
        return User.builder()
                .id(1L)
                .email("email@email.com")
                .username("username")
                .password("password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(true)
                .build();
    }
}
