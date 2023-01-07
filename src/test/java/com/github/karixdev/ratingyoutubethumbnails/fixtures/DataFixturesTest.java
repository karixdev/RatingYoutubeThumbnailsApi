package com.github.karixdev.ratingyoutubethumbnails.fixtures;

import com.github.karixdev.ratingyoutubethumbnails.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnails.thumbnail.ThumbnailRepository;
import com.github.karixdev.ratingyoutubethumbnails.user.User;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRepository;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnails.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataFixturesTest {
    @InjectMocks
    DataFixtures underTest;

    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    ThumbnailRepository thumbnailRepository;

    @Mock
    FixturesProperties properties;

    @Test
    void shouldNotLoadDataFixtures() {
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldNotCreateUserIfEmailIsTaken() {
        when(userRepository.findByEmail(eq("admin@admin.com")))
                .thenReturn(Optional.of(User.builder()
                        .email("admin@admin.com")
                        .password("admin213")
                        .username("admin")
                        .userRole(UserRole.ROLE_USER)
                        .isEnabled(true)
                        .build()));

        when(properties.getLoadFixtures())
                .thenReturn(true);

        underTest.run();

        verify(userService, never())
                .createUser(any(), any(), any(), any(), any());
    }

    @Test
    void shouldNotCreateUserIfUsernameIsTaken() {
        when(userRepository.findByEmail(eq("admin@admin.com")))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(eq("admin")))
                .thenReturn(Optional.of(User.builder()
                        .email("admin@admin.com")
                        .password("admin213")
                        .username("admin")
                        .userRole(UserRole.ROLE_USER)
                        .isEnabled(true)
                        .build()));

        when(properties.getLoadFixtures())
                .thenReturn(true);

        underTest.run();

        verify(userService, never())
                .createUser(any(), any(), any(), any(), any());
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.findByEmail(eq("admin@admin.com")))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(eq("admin")))
                .thenReturn(Optional.empty());

        when(properties.getLoadFixtures())
                .thenReturn(true);

        User user = User.builder()
                .email("admin@admin.com")
                .password("admin213")
                .username("admin")
                .userRole(UserRole.ROLE_ADMIN)
                .isEnabled(true)
                .build();

        when(userService.createUser(any(), any(), any(), any(), any())).thenReturn(user);

        underTest.run();

        verify(userService, times(1))
                .createUser(
                        eq("admin@admin.com"),
                        eq("admin"),
                        eq("admin123"),
                        eq(UserRole.ROLE_ADMIN),
                        eq(true)
                );
    }

    @Test
    void shouldNotCreateThumbnailsIfTheyAlreadyExist() {
        when(userRepository.findByEmail(eq("admin@admin.com")))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(eq("admin")))
                .thenReturn(Optional.empty());

        when(properties.getLoadFixtures())
                .thenReturn(true);

        User user = User.builder()
                .email("admin@admin.com")
                .password("admin213")
                .username("admin")
                .userRole(UserRole.ROLE_ADMIN)
                .isEnabled(true)
                .build();

        when(userService.createUser(any(), any(), any(), any(), any()))
                .thenReturn(user);

        when(thumbnailRepository.findByYoutubeVideoId(any()))
                .thenReturn(Optional.of(Thumbnail.builder()
                        .id(1L)
                        .addedBy(user)
                        .url("thumbnail-url")
                        .youtubeVideoId("youtube-id")
                        .build()));

        underTest.run();

        verify(thumbnailRepository, never()).save(any());
    }

    @Test
    void shouldThumbnails() {
        when(userRepository.findByEmail(eq("admin@admin.com")))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(eq("admin")))
                .thenReturn(Optional.empty());

        when(properties.getLoadFixtures())
                .thenReturn(true);

        User user = User.builder()
                .email("admin@admin.com")
                .password("admin213")
                .username("admin")
                .userRole(UserRole.ROLE_ADMIN)
                .isEnabled(true)
                .build();

        when(userService.createUser(any(), any(), any(), any(), any()))
                .thenReturn(user);

        when(thumbnailRepository.findByYoutubeVideoId(any()))
                .thenReturn(Optional.empty());

        underTest.run();

        verify(thumbnailRepository, times(7)).save(any());
    }
}
