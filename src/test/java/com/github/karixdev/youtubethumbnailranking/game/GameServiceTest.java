package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.game.exception.GameHasEndedException;
import com.github.karixdev.youtubethumbnailranking.game.exception.GameHasNotEndedException;
import com.github.karixdev.youtubethumbnailranking.game.exception.InvalidWinnerIdException;
import com.github.karixdev.youtubethumbnailranking.game.payload.request.GameResultRequest;
import com.github.karixdev.youtubethumbnailranking.game.payload.response.GameResponse;
import com.github.karixdev.youtubethumbnailranking.rating.RatingService;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.shared.exception.PermissionDeniedException;
import com.github.karixdev.youtubethumbnailranking.shared.exception.ResourceNotFoundException;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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
    void GivenUserPrincipal_WhenStart_ThenReturnsCorrectGameResponse() {
        // Given
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        when(repository.save(any()))
                .thenReturn(Game.builder()
                        .id(1L)
                        .user(user)
                        .thumbnail1(thumbnail1)
                        .thumbnail2(thumbnail2)
                        .lastActivity(NOW.toLocalDateTime())
                        .build());

        // When
        GameResponse result = underTest.start(userPrincipal);

        // Then
        assertThat(result.getId()).isEqualTo(1L);

        assertThat(result.getThumbnail1().getId()).isEqualTo(1L);
        assertThat(result.getThumbnail1().getUrl()).isEqualTo("thumbnail-url-1");

        assertThat(result.getThumbnail2().getId()).isEqualTo(2L);
        assertThat(result.getThumbnail2().getUrl()).isEqualTo("thumbnail-url-2");

        verify(repository).save(any());
    }

    @Test
    void GivenUserPrincipalThatHasJustStartedGame_WhenStart_ThenThrowsGameHasNotEndedExceptionWithCorrectMessage() {
        // Given
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        when(repository.findByUserOrderByLastActivityDesc(any()))
                .thenReturn(List.of(Game.builder()
                        .id(1L)
                        .user(user)
                        .thumbnail1(thumbnail1)
                        .thumbnail2(thumbnail2)
                        .lastActivity(NOW.toLocalDateTime().plusMinutes(1))
                        .build()));

        when(properties.getDuration()).thenReturn(10);

        // When & Then
        assertThatThrownBy(() -> underTest.start(userPrincipal))
                .isInstanceOf(GameHasNotEndedException.class)
                .hasMessage("Game has not ended yet");
    }

    @Test
    void GivenNotExistingGameId_WhenResult_ThenThrowsResourceNotFoundExceptionWithCorrectMessage() {
        // Given
        Long gameId = 1337L;

        UserPrincipal userPrincipal = new UserPrincipal(user);
        GameResultRequest payload = new GameResultRequest();

        when(repository.findById(any()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.result(gameId, payload, userPrincipal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Game with provided id was not found");
    }

    @Test
    void GivenUserThatIsNotOwnerOfGame_WhenResult_ThenThrowsPermissionDeniedExceptionWithCorrectMessage() {
        // Given
        Long gameId = 1L;
        GameResultRequest payload = new GameResultRequest(1L);
        UserPrincipal userPrincipal = new UserPrincipal(User.builder()
                .id(2L)
                .email("email-2@email.com")
                .password("password-2")
                .username("username-2")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build());

        when(repository.findById(any()))
                .thenReturn(Optional.of(Game.builder()
                        .id(1L)
                        .user(user)
                        .thumbnail1(thumbnail1)
                        .thumbnail2(thumbnail2)
                        .lastActivity(NOW.toLocalDateTime().minusMinutes(2))
                        .build()));

        // When & Then
        assertThatThrownBy(() -> underTest.result(gameId, payload, userPrincipal))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessage("You are not the owner of the game");
    }

    @Test
    void GivenEndedGameId_WhenResult_ThenThrowsGameHasEndedExceptionWithCorrectMessage() {
        // Given
        Long gameId = 1L;
        GameResultRequest payload = new GameResultRequest(1L);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(properties.getDuration())
                .thenReturn(10);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        when(repository.findById(any()))
                .thenReturn(Optional.of(Game.builder()
                        .id(1L)
                        .user(user)
                        .thumbnail1(thumbnail1)
                        .thumbnail2(thumbnail2)
                        .lastActivity(NOW.toLocalDateTime().minusMinutes(30))
                        .build()));

        // When & Then
        assertThatThrownBy(() -> underTest.result(gameId, payload, userPrincipal))
                .isInstanceOf(GameHasEndedException.class)
                .hasMessage("Game has ended");
    }

    @Test
    void GivenInvalidPayload_WhenResult_ThenInvalidWinnerIdExceptionWithCorrectMessage() {
        // Given
        Long gameId = 1L;
        GameResultRequest payload = new GameResultRequest(3L);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(properties.getDuration())
                .thenReturn(10);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        when(repository.findById(any()))
                .thenReturn(Optional.of(Game.builder()
                        .id(1L)
                        .user(user)
                        .thumbnail1(thumbnail1)
                        .thumbnail2(thumbnail2)
                        .lastActivity(NOW.toLocalDateTime().minusMinutes(1))
                        .build()));

        // When & Then
        assertThatThrownBy(() -> underTest.result(gameId, payload, userPrincipal))
                .isInstanceOf(InvalidWinnerIdException.class)
                .hasMessage("You have provided invalid winner id");
    }

    @Test
    void GivenValidGameIdAndValidPayloadAndValidUserPrincipal_WhenResult_ThenReturnsProperGameResponse() {
        // Given
        Long gameId = 1L;
        GameResultRequest payload = new GameResultRequest(1L);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        when(properties.getDuration())
                .thenReturn(10);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        when(repository.findById(any()))
                .thenReturn(Optional.of(Game.builder()
                        .id(1L)
                        .user(user)
                        .thumbnail1(thumbnail1)
                        .thumbnail2(thumbnail2)
                        .lastActivity(NOW.toLocalDateTime().minusMinutes(1))
                        .build()));

        Thumbnail otherThumbnail = Thumbnail.builder()
                .id(3L)
                .addedBy(user)
                .url("thumbnail-url-3")
                .youtubeVideoId("youtube-id-3")
                .build();


        when(repository.save(any())).thenReturn(Game.builder()
                .id(1L)
                .user(user)
                .thumbnail1(thumbnail1)
                .thumbnail2(otherThumbnail)
                .lastActivity(NOW.toLocalDateTime().minusMinutes(1))
                .build());

        // When
        GameResponse result = underTest.result(gameId, payload, userPrincipal);

        // Then
        verify(ratingService).updateRatings(eq(thumbnail1), eq(thumbnail2), eq(user));

        assertThat(result.getId()).isEqualTo(1L);

        assertThat(result.getThumbnail1().getUrl()).isEqualTo("thumbnail-url-1");
        assertThat(result.getThumbnail1().getId()).isEqualTo(1L);

        assertThat(result.getThumbnail2().getUrl()).isEqualTo("thumbnail-url-3");
        assertThat(result.getThumbnail2().getId()).isEqualTo(3L);
    }
}
