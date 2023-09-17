package com.github.karixdev.ratingyoutubethumbnailsapi.utils;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.entity.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.constants.RatingConstants;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.entity.EntityState;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestUtils {
    public static UserDTO createUserDTO() {
        return new UserDTO(
                UUID.randomUUID(),
                "email@email.com",
                "username",
                UserRole.USER,
                "password"
        );
    }

    public static UserDTO createAdminUserDTO() {
        return new UserDTO(
                UUID.randomUUID(),
                "email@email.com",
                "username",
                UserRole.ADMIN,
                "password"
        );
    }

    public static YoutubeVideoDTO createYoutubeVideoDTO(String id) {
        return new YoutubeVideoDTO(
                id,
                "default",
                "medium",
                "high",
                "standard",
                "max"
        );
    }

    public static Video createVideo(UUID id, String youtubeId, UUID userId, LocalDateTime createdAt) {
        return createVideo(id, youtubeId, userId, createdAt, EntityState.PERSISTED);
    }

    public static Video createVideo(UUID id, String youtubeId, UUID userId, LocalDateTime createdAt, EntityState state) {
        return Video.builder()
                .id(id)
                .userId(userId)
                .youtubeId(youtubeId)
                .defaultResThumbnail("default")
                .mediumResThumbnail("medium")
                .highResThumbnail("high")
                .standardResThumbnail("standard")
                .maxResThumbnail("max")
                .state(state)
                .createdAt(createdAt)
                .build();
    }

    public static Video createVideo(String ytId, UUID userId) {
        return createVideo(null, ytId, userId, LocalDateTime.now());
    }

    public static Video createVideo() {
        return createVideo(UUID.randomUUID(), "youtube-id", UUID.randomUUID(), LocalDateTime.now());
    }

    public static User createUser() {
        return User.builder()
                .email("email@email.com")
                .username("username")
                .password("password")
                .role(UserRole.USER)
                .build();
    }

    public static User createAdmin() {
        return User.builder()
                .email("admin@admin.com")
                .username("admin")
                .password("password")
                .role(UserRole.ADMIN)
                .build();
    }

    public static String ytApiSuccessResponse() {
        return """
                {
                    "items": [
                        {
                            "kind": "kind",
                            "id": "id",
                            "snippet": {
                                "title": "title",
                                "description": "description",
                                "thumbnails": {
                                    "default": {
                                             "url": "default",
                                             "width": 100,
                                             "height": 100
                                    },
                                    "medium": {
                                             "url": "medium",
                                             "width": 100,
                                             "height": 100
                                    },
                                    "high": {
                                             "url": "high",
                                             "width": 100,
                                             "height": 100
                                    },
                                    "standard": {
                                             "url": "standard",
                                             "width": 100,
                                             "height": 100
                                    },
                                    "maxres": {
                                             "url": "max",
                                             "width": 100,
                                             "height": 100
                                    }
                                }
                            }
                        }
                    ]
                }
                """;
    }

    public static Rating createRating(UUID id, UUID userId, UUID videoId, BigDecimal points) {
        return Rating.builder()
                .id(id)
                .userId(userId)
                .videoId(videoId)
                .points(points)
                .build();
    }

    public static Rating createRating(UUID userId, UUID videoId, BigDecimal points) {
        return Rating.builder()
                .userId(userId)
                .videoId(videoId)
                .points(points)
                .build();
    }

}
