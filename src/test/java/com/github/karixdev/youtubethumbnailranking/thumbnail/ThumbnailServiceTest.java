package com.github.karixdev.youtubethumbnailranking.thumbnail;

import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailRepository;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.thumnail.exception.ThumbnailAlreadyExistsException;
import com.github.karixdev.youtubethumbnailranking.thumnail.payload.request.ThumbnailRequest;
import com.github.karixdev.youtubethumbnailranking.thumnail.payload.response.ThumbnailResponse;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import com.github.karixdev.youtubethumbnailranking.youtube.YoutubeVideoService;
import com.github.karixdev.youtubethumbnailranking.youtube.payload.response.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThumbnailServiceTest {
    @InjectMocks
    ThumbnailService underTest;

    @Mock
    ThumbnailRepository thumbnailRepository;

    @Mock
    YoutubeVideoService youtubeVideoService;

    Thumbnail thumbnail;

    UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        userPrincipal = new UserPrincipal(user);

        thumbnail = Thumbnail.builder()
                .id(1L)
                .addedBy(user)
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .build();
    }

    @Test
    void GivenAlreadyExistingThumbnailYoutubeId_WhenAdd_ThenThrowsThumbnailAlreadyExistsException() {
        // Given
        ThumbnailRequest payload = new ThumbnailRequest("youtube-id");

        when(thumbnailRepository.findByYoutubeVideoId(any()))
                .thenReturn(Optional.of(thumbnail));

        // When & Then
        assertThatThrownBy(() -> underTest.add(payload, userPrincipal))
                .isInstanceOf(ThumbnailAlreadyExistsException.class)
                .hasMessage("Thumbnail with provided youtube video id already exists");
    }

    @Test
    void GivenValidYoutubeId_WhenAdd_ThenReturnsCorrectThumbnailResponse() {
        // Given
        ThumbnailRequest payload = new ThumbnailRequest("youtube-id-2");

        when(thumbnailRepository.findByYoutubeVideoId(any()))
                .thenReturn(Optional.empty());

        Thumbnails thumbnails = new Thumbnails(
                new Default("default-url", 100, 100),
                new Medium("medium-url", 100, 100),
                new High("high-url", 100, 100),
                new Standard("standard-url", 100, 100),
                new Maxres("thumbnail-url", 100, 100)
        );

        when(youtubeVideoService.getVideoDetails(any()))
                .thenReturn(new Item(
                        "youtube#video",
                        "youtube-id-2",
                        new Snippet(
                                "title",
                                "description",
                                thumbnails
                        )
                ));

        when(thumbnailRepository.save(any()))
                .thenReturn(thumbnail);

        // When
        ThumbnailResponse result = underTest.add(payload, userPrincipal);

        // Then
        assertThat(result).isEqualTo(new ThumbnailResponse(thumbnail));

        verify(youtubeVideoService).getVideoDetails(any());
    }
}
