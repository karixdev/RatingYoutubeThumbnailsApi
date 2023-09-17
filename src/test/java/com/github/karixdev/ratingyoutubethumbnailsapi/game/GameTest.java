package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.UnitTestDataUtil;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasAlreadyEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.Round;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.exception.EmptyRoundSetException;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.PermissionDeniedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameTest {
    @Mock
    Clock clock;

    @Test
    void GivenThumbnailsAndClock_WhenAddRound_ThenCorrectRoundIsCreatedAndAddedToSet() {
        // Given
        Game underTest = Game.builder().id(1L).build();

        Thumbnail thumbnail1 = Thumbnail.builder().id(1L).build();
        Thumbnail thumbnail2 = Thumbnail.builder().id(1L).build();

        when(clock.getZone()).thenReturn(UnitTestDataUtil.createZonedDateTime().getZone());
        when(clock.instant()).thenReturn(UnitTestDataUtil.createZonedDateTime().toInstant());

        // When
        underTest.addRound(thumbnail1, thumbnail2, clock);

        // Then
        assertThat(underTest.getRounds()).hasSize(1);

        Round round = underTest.getRounds().stream().toList().get(0);

        assertThat(round.getThumbnail1()).isEqualTo(thumbnail1);
        assertThat(round.getThumbnail2()).isEqualTo(thumbnail2);
        assertThat(round.getGame()).isEqualTo(underTest);
        assertThat(round.getCreatedAt()).isEqualTo(LocalDateTime.of(
                2022,
                11,
                23,
                13,
                44,
                30
        ));
    }

    @Test
    void WhenGetLatestRound_ThenThrowsEmptyRoundSetExceptionIfSetIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> new Game().getLatestRound())
                .isInstanceOf(EmptyRoundSetException.class);
    }

    @Test
    void WhenGetLatestRound_ThenReturnsLatestRound() {
        LocalDateTime dt = LocalDateTime.of(2023, 1, 1, 1, 1);

        Round round1 = Round.builder()
                .id(UUID.randomUUID())
                .createdAt(dt)
                .build();

        Round round2 = Round.builder()
                .id(UUID.randomUUID())
                .createdAt(dt.plusHours(10))
                .build();

        Game underTest = Game.builder()
                .rounds(Set.of(round1, round2))
                .build();

        // When
        Round result = underTest.getLatestRound();

        // Then
        assertThat(result).isEqualTo(round2);
    }

    @Test
    void GivenClockAndMaxTimeOfNoGameUpdate_WhenIsGameExpired_ThenReturnsTrueIfGameHasEnded() {
        // Given
        Game underTest = Game.builder().hasEnded(true).build();

        int maxTimeOfNoGameUpdate = 10;

        // When
        boolean result = underTest.isGameExpired(clock, maxTimeOfNoGameUpdate);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void GivenClockAfterGameExpirationTimeAndMaxTimeOfNoGameUpdate_WhenIsGameExpired_ThenReturnsTrue() {
        // Given
        LocalDateTime lastActivity = LocalDateTime.of(
                2023, 1, 1, 1, 0
        );
        Game underTest = Game.builder().lastActivity(lastActivity).build();

        int maxTimeOfNoGameUpdate = 10;

        when(clock.getZone()).thenReturn(ZoneId.of("UTC+0"));
        when(clock.instant()).thenReturn(lastActivity.plusMinutes(maxTimeOfNoGameUpdate + 1).toInstant(ZoneOffset.of("+0")));

        // When
        boolean result = underTest.isGameExpired(clock, maxTimeOfNoGameUpdate);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void GivenClockBeforeGameExpirationTimeAndMaxTimeOfNoGameUpdate_WhenIsGameExpired_ThenReturnsFalse() {
        // Given
        LocalDateTime lastActivity = LocalDateTime.of(
                2023, 1, 1, 1, 0
        );
        Game underTest = Game.builder().lastActivity(lastActivity).build();

        int maxTimeOfNoGameUpdate = 10;

        when(clock.getZone()).thenReturn(ZoneId.of("UTC+0"));
        when(clock.instant()).thenReturn(lastActivity.plusMinutes(maxTimeOfNoGameUpdate - 3).toInstant(ZoneOffset.of("+0")));

        // When
        boolean result = underTest.isGameExpired(clock, maxTimeOfNoGameUpdate);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void GivenUserThatIsNotOwnerOfGameNorIsAdmin_WhenEnd_ThenThrowsPermissionDeniedExceptionWithCorrectMessage() {
        // Given
        Game underTest = Game.builder()
                .user(createUser(1L, UserRole.ROLE_USER))
                .build();

        User user = createUser(2L, UserRole.ROLE_USER);

        // When & Then
        assertThatThrownBy(() -> underTest.endGame(user, clock, 10))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessage("You are not the owner of the game");
    }

    @Test
    void test1HasEndedTrue() {
        // Given
        User user = createUser(1L, UserRole.ROLE_USER);
        Game underTest = Game.builder()
                .user(user)
                .hasEnded(true)
                .build();

        // When & Then
        assertThatThrownBy(() -> underTest.endGame(user, clock, 10))
                .isInstanceOf(GameHasAlreadyEndedException.class)
                .hasMessage("Game has already ended");
    }

    @Test
    void test2Expired() {
        // Given
        User user = createUser(1L, UserRole.ROLE_USER);
        LocalDateTime lastActivity = LocalDateTime.of(2023, 1, 1, 1, 1);
        int maxTimeOfNoGameUpdate = 10;

        Game underTest = Game.builder()
                .user(user)
                .hasEnded(false)
                .lastActivity(lastActivity)
                .build();

        when(clock.getZone()).thenReturn(ZoneId.of("UTC+0"));
        when(clock.instant()).thenReturn(lastActivity.plusMinutes(maxTimeOfNoGameUpdate + 1).toInstant(ZoneOffset.of("+0")));

        // When & Then
        assertThatThrownBy(() -> underTest.endGame(user, clock, maxTimeOfNoGameUpdate))
                .isInstanceOf(GameHasAlreadyEndedException.class)
                .hasMessage("Game has already ended");
    }

    @Test
    void test2Ends() {
        // Given
        User user = createUser(1L, UserRole.ROLE_USER);
        LocalDateTime lastActivity = LocalDateTime.of(2023, 1, 1, 1, 1);
        int maxTimeOfNoGameUpdate = 10;

        Game underTest = Game.builder()
                .user(user)
                .hasEnded(false)
                .lastActivity(lastActivity)
                .build();

        when(clock.getZone()).thenReturn(ZoneId.of("UTC+0"));
        when(clock.instant()).thenReturn(lastActivity.toInstant(ZoneOffset.of("+0")));

        // When
        underTest.endGame(user, clock, maxTimeOfNoGameUpdate);

        // Then
        assertThat(underTest.getHasEnded()).isTrue();
    }

    @Test
    void test2EndsAdmin() {
        // Given
        User user = createUser(1L, UserRole.ROLE_ADMIN);
        LocalDateTime lastActivity = LocalDateTime.of(2023, 1, 1, 1, 1);
        int maxTimeOfNoGameUpdate = 10;

        Game underTest = Game.builder()
                .user(createUser(2L, UserRole.ROLE_USER))
                .hasEnded(false)
                .lastActivity(lastActivity)
                .build();

        when(clock.getZone()).thenReturn(ZoneId.of("UTC+0"));
        when(clock.instant()).thenReturn(lastActivity.toInstant(ZoneOffset.of("+0")));

        // When
        underTest.endGame(user, clock, maxTimeOfNoGameUpdate);

        // Then
        assertThat(underTest.getHasEnded()).isTrue();
    }

    private static User createUser(Long id, UserRole role) {
        return User.builder()
                .id(id)
                .userRole(role)
                .build();
    }
}