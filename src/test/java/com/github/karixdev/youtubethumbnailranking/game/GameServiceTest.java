package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.game.payload.response.GameResponse;
import com.github.karixdev.youtubethumbnailranking.rating.RatingService;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

}
