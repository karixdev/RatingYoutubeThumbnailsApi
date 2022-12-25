package com.github.karixdev.youtubethumbnailranking.rating.thumbnail;

import com.github.karixdev.youtubethumbnailranking.rating.Rating;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.shared.exception.PermissionDeniedException;
import com.github.karixdev.youtubethumbnailranking.shared.exception.ResourceNotFoundException;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailRepository;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.thumnail.exception.EmptyThumbnailsListException;
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

import java.util.*;

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

    @Test
    void GivenNotExistingThumbnailId_WhenDelete_ThenThrowsResourceNotFoundExceptionWithCorrectMessage() {
        // Given
        Long id = 4L;

        when(thumbnailRepository.findById(any()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.delete(id, userPrincipal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Thumbnail with provided id does not found");
    }

    @Test
    void GivenUserPrincipalWhoIsNotAnAdminAndNotAndAuthorOfThumbnail_WhenDelete_ThenThrowsPermissionDeniedExceptionWithCorrectMessage() {
        // Given
        Long id = 1L;

        UserPrincipal otherUserPrincipal = new UserPrincipal(
                User.builder()
                        .email("email-2@email.com")
                        .username("username-2")
                        .password("password-2")
                        .isEnabled(Boolean.TRUE)
                        .userRole(UserRole.ROLE_USER)
                        .build()
        );

        when(thumbnailRepository.findById(any()))
                .thenReturn(Optional.of(thumbnail));

        // When & Then
        assertThatThrownBy(() -> underTest.delete(id, otherUserPrincipal))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessage("Cannot delete thumbnail with provided id");
    }

    @Test
    void GivenAdminUserPrincipalWhoIsNotAuthorOfThumbnail_WhenDelete_ThenReturnsSuccessResponse() {
        // Given
        Long id = 1L;

        UserPrincipal adminUserPrincipal = new UserPrincipal(
                User.builder()
                        .email("email-2@email.com")
                        .username("username-2")
                        .password("password-2")
                        .isEnabled(Boolean.TRUE)
                        .userRole(UserRole.ROLE_ADMIN)
                        .build()
        );

        when(thumbnailRepository.findById(any()))
                .thenReturn(Optional.of(thumbnail));

        // When
        SuccessResponse result = underTest.delete(id, adminUserPrincipal);

        // Then
        assertThat(result.getMessage()).isEqualTo("success");

        verify(thumbnailRepository).delete(eq(thumbnail));
    }

    @Test
    void GivenUserPrincipalWhoIsAuthorOfThumbnail_WhenDelete_ThenReturnsSuccessResponse() {
        // Given
        Long id = 1L;

        when(thumbnailRepository.findById(any()))
                .thenReturn(Optional.of(thumbnail));

        // When
        SuccessResponse result = underTest.delete(id, userPrincipal);

        // Then
        assertThat(result.getMessage()).isEqualTo("success");

        verify(thumbnailRepository).delete(eq(thumbnail));
    }

    @Test
    void GivenEmptyThumbnailList_WhenGetRandomThumbnailFromList_ThenThrowsEmptyThumbnailsListExceptionWithCorrectMessage() {
        // Given
        List<Thumbnail> thumbnails = List.of();

        // When & Then
        assertThatThrownBy(() -> underTest.getRandomThumbnailFromList(thumbnails))
                .isInstanceOf(EmptyThumbnailsListException.class)
                .hasMessage("Provided list with thumbnails is empty");
    }

    @Test
    void GivenNotEmptyThumbnailList_WhenGetRandomThumbnailFromList_ThenReturnsRandomThumbnailFromThumbnailList() {
        // Given
        List<Thumbnail> thumbnails = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            thumbnails.add(Thumbnail.builder()
                    .id((long) i)
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());
        }

        // When
        Thumbnail result = underTest.getRandomThumbnailFromList(thumbnails);

        // Then
        assertThat(thumbnails).contains(result);
    }

    @Test
    void GivenEmptyRepository_WhenGetRandomThumbnail_ThenThrowsEmptyThumbnailsListExceptionWithCorrectMessage() {
        // Given
        when(thumbnailRepository.findAll())
                .thenReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> underTest.getRandomThumbnail())
                .isInstanceOf(EmptyThumbnailsListException.class)
                .hasMessage("Provided list with thumbnails is empty");
    }

    @Test
    void GivenNotEmptyRepository_WhenGetRandomThumbnail_ThenReturnsRandomThumbnailFromRepositoryList() {
        // Given
        List<Thumbnail> thumbnails = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            thumbnails.add(Thumbnail.builder()
                    .id((long) i)
                    .addedBy(userPrincipal.getUser())
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());
        }

        when(thumbnailRepository.findAll())
                .thenReturn(thumbnails);

        // When
        Thumbnail result = underTest.getRandomThumbnail();

        // Then
        assertThat(thumbnails).contains(result);
    }

    @Test
    void GivenUser_WhenGetThumbnailsWithoutUserRating_ThenReturnsCorrectList() {
        // Given
        User user = userPrincipal.getUser();

        when(thumbnailRepository.findAll())
                .thenReturn(List.of(
                        thumbnail,
                        Thumbnail.builder()
                                .id(2L)
                                .addedBy(user)
                                .url("thumbnail-url-2")
                                .youtubeVideoId("youtube-id-2")
                                .ratings(Set.of(Rating.builder()
                                        .user(userPrincipal.getUser())
                                        .build()))
                                .build()
                ));

        // When
        List<Thumbnail> result = underTest.getThumbnailsWithoutUserRating(user);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(thumbnail);
    }
}
