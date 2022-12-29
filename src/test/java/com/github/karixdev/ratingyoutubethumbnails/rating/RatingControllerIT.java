package com.github.karixdev.ratingyoutubethumbnails.rating;

import com.github.karixdev.ratingyoutubethumbnails.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnails.jwt.JwtService;
import com.github.karixdev.ratingyoutubethumbnails.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnails.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnails.thumbnail.ThumbnailRepository;
import com.github.karixdev.ratingyoutubethumbnails.user.User;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRepository;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnails.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class RatingControllerIT extends ContainersEnvironment {
    @Autowired
    WebTestClient webClient;

    @Autowired
    UserService userService;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    RatingProperties ratingProperties;

    @AfterEach
    void tearDown() {
        ratingRepository.deleteAll();
        thumbnailRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldRespondWith404IfThumbnailDoesNotExist() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        webClient.get().uri("/api/v1/rating/do-not-exist")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldRespondWith200AndPointsAtBaseLevelBecauseThumbnailHasNotBeenRatedYet() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        String token = jwtService.createToken(userPrincipal);

        thumbnailRepository.save(Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(userPrincipal.getUser())
                .build());

        webClient.get().uri("/api/v1/rating/youtube-id")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.global_rating_points").isEqualTo(ratingProperties.getBasePoints())
                .jsonPath("$.user_rating_points").isEqualTo(ratingProperties.getBasePoints());
    }

    @Test
    void shouldRespondWith200AndCalculatedAverageGlobalRatingPoints() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        User otherUser = userService.createUser(
                "email-2@email.pl",
                "username-2",
                "password",
                UserRole.ROLE_USER,
                Boolean.TRUE
        );

        String token = jwtService.createToken(userPrincipal);

        Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(userPrincipal.getUser())
                .build());

        ratingRepository.save(Rating.builder()
                .thumbnail(thumbnail)
                .user(userPrincipal.getUser())
                .points(new BigDecimal(1400))
                .build());

        ratingRepository.save(Rating.builder()
                .thumbnail(thumbnail)
                .user(otherUser)
                .points(new BigDecimal(1600))
                .build());

        webClient.get().uri("/api/v1/rating/youtube-id")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.global_rating_points").isEqualTo(new BigDecimal("1500.0"))
                .jsonPath("$.user_rating_points").isEqualTo(new BigDecimal("1400.0"));
    }

    @Test
    void shouldRespondWith200AndNullUserGlobalRatingPoints() {
        UserPrincipal userPrincipal = new UserPrincipal(
                userService.createUser(
                        "email@email.pl",
                        "username",
                        "password",
                        UserRole.ROLE_USER,
                        Boolean.TRUE
                ));

        User otherUser = userService.createUser(
                "email-2@email.pl",
                "username-2",
                "password",
                UserRole.ROLE_USER,
                Boolean.TRUE
        );

        Thumbnail thumbnail = thumbnailRepository.save(Thumbnail.builder()
                .url("thumbnail-url")
                .youtubeVideoId("youtube-id")
                .addedBy(userPrincipal.getUser())
                .build());

        ratingRepository.save(Rating.builder()
                .thumbnail(thumbnail)
                .user(userPrincipal.getUser())
                .points(new BigDecimal(1400))
                .build());

        ratingRepository.save(Rating.builder()
                .thumbnail(thumbnail)
                .user(otherUser)
                .points(new BigDecimal(1600))
                .build());

        webClient.get().uri("/api/v1/rating/youtube-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.global_rating_points").isEqualTo(new BigDecimal("1500.0"))
                .jsonPath("$.user_rating_points").isEmpty();
    }
}
