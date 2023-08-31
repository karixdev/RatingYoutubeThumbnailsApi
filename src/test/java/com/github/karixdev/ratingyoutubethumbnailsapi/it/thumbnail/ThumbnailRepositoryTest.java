package com.github.karixdev.ratingyoutubethumbnailsapi.it.thumbnail;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.Round;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.ThumbnailRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ThumbnailRepositoryTest extends ContainersEnvironment {
    @Autowired
    ThumbnailRepository underTest;

    @Autowired
    TestEntityManager em;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void GivenNotExistingThumbnailYoutubeId_WhenFindByYoutubeId_ThenReturnsEmptyOptional() {
        // Given
        String youtubeId = "i-do-not-exist";

        User user = createUser();
        em.persist(user);
        em.persist(createThumbnail("thumbnail-url", "yt-id", user));

        // When
        Optional<Thumbnail> result = underTest.findByYoutubeVideoId(youtubeId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenExistingThumbnailYoutubeId_WhenFindByYoutubeId_ThenReturnsOptionalWithCorrectEntity() {
        // Given
        User user = createUser();
        em.persist(user);

        Thumbnail thumbnail = createThumbnail("thumbnail-url", "yt-id", user);
        em.persist(thumbnail);

        String youtubeId = thumbnail.getYoutubeVideoId();

        // When
        Optional<Thumbnail> result = underTest.findByYoutubeVideoId(youtubeId);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(thumbnail);
    }

    @Test
    void WhenFindAllThumbnails_ThenReturnsCorrectList() {
        User user = createUser();
        em.persist(user);

        Thumbnail thumbnail = createThumbnail("thumbnail-url", "yt-id", user);
        em.persist(thumbnail);

        // When
        List<Thumbnail> result = underTest.findAllThumbnails();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(thumbnail);
    }

    @Test
    void GivenThumbnailsWithRatingsWhereNoneWereInGame_WhenFindThumbnailNotInGameWithClosestRating_ThenReturnsThumbnailWithClosestRating() {
        // Given
        User user = createUser();
        em.persist(user);

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        Rating rating1 = createRating(user, thumbnail1, 1500);

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);
        createRating(user, thumbnail2, 1450);

        Thumbnail thumbnail3 = createThumbnail("thumbnail-url3", "yt-id3", user);
        createRating(user, thumbnail3, 1800);

        em.persist(thumbnail1);
        em.persist(thumbnail2);
        em.persist(thumbnail3);

        Game game = Game.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .user(user)
                .lastActivity(LocalDateTime.now())
                .build();

        em.persist(game);

        // When
        Optional<Thumbnail> result = underTest.findThumbnailNotInGameWithClosestRating(
                game.getId(),
                user.getId(),
                BigDecimal.valueOf(1400),
                thumbnail1.getId(),
                rating1.getPoints()
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(thumbnail2);
    }

    @Test
    void GivenThumbnailsWhereTwoWereAlreadyInRound_WhenFindThumbnailNotInGameWithClosestRating_ThenOptionalWithCorrectThumbnailIsReturned() {
        // Given
        User user = createUser();
        em.persist(user);

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        Rating rating1 = createRating(user, thumbnail1, 1500);
        em.persist(thumbnail1);

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);
        createRating(user, thumbnail2, 1450);
        em.persist(thumbnail2);

        Game game = Game.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .user(user)
                .lastActivity(LocalDateTime.now())
                .build();
        em.persist(game);

        Round round = createRound(game, thumbnail1, thumbnail2);
        em.persist(round);

        Thumbnail thumbnail3 = createThumbnail("thumbnail-url3", "yt-id3", user);
        createRating(user, thumbnail3, 1800);
        em.persist(thumbnail3);

        // When
        Optional<Thumbnail> result = underTest.findThumbnailNotInGameWithClosestRating(
                game.getId(),
                user.getId(),
                BigDecimal.valueOf(1400),
                thumbnail1.getId(),
                rating1.getPoints()
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(thumbnail3);
    }

    @Test
    void GivenThumbnailsWhereOneWasInAllCombinations_WhenFindThumbnailNotInGameWithClosestRating_ThenEmptyOptionalIsReturned() {
        // Given
        User user = createUser();
        em.persist(user);

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        Rating rating1 = createRating(user, thumbnail1, 1500);
        em.persist(thumbnail1);

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);
        createRating(user, thumbnail2, 1450);
        em.persist(thumbnail2);

        Game game = Game.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .user(user)
                .lastActivity(LocalDateTime.now())
                .build();
        em.persist(game);

        Thumbnail thumbnail3 = createThumbnail("thumbnail-url3", "yt-id3", user);
        createRating(user, thumbnail3, 1800);
        em.persist(thumbnail3);

        em.persist(createRound(game, thumbnail1, thumbnail2));
        em.persist(createRound(game, thumbnail3, thumbnail1));

        // When
        Optional<Thumbnail> result = underTest.findThumbnailNotInGameWithClosestRating(
                game.getId(),
                user.getId(),
                BigDecimal.valueOf(1400),
                thumbnail1.getId(),
                rating1.getPoints()
        );

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenThumbnailsWhereOneWasInAllCombinations_WhenFindThumbnailNotInGameWithClosestRating_ThenOptionalWithCorrectThumbnailIsReturnedForOtherThumbnail() {
        // Given
        User user = createUser();
        em.persist(user);

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        createRating(user, thumbnail1, 1500);
        em.persist(thumbnail1);

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);
        Rating rating2 = createRating(user, thumbnail2, 1450);
        em.persist(thumbnail2);

        Game game = Game.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .user(user)
                .lastActivity(LocalDateTime.now())
                .build();
        em.persist(game);

        Round round = createRound(game, thumbnail1, thumbnail2);
        em.persist(round);

        Thumbnail thumbnail3 = createThumbnail("thumbnail-url3", "yt-id3", user);
        createRating(user, thumbnail3, 1800);
        em.persist(thumbnail3);

        // When
        Optional<Thumbnail> result = underTest.findThumbnailNotInGameWithClosestRating(
                game.getId(),
                user.getId(),
                BigDecimal.valueOf(1400),
                thumbnail2.getId(),
                rating2.getPoints()
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(thumbnail3);
    }

    @Test
    void GivenThumbnailsWhereOneHasNotBeenRatedYetByUserAndDefaultRatingIsClosestIToPickedThumbnail_WhenFindThumbnailNotInGameWithClosestRating_ThenOptionalWithCorrectThumbnailIsReturned() {
        // Given
        User user = createUser();
        em.persist(user);

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        Rating rating1 = createRating(user, thumbnail1, 1500);

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);

        Thumbnail thumbnail3 = createThumbnail("thumbnail-url3", "yt-id3", user);
        createRating(user, thumbnail3, 1800);

        em.persist(thumbnail1);
        em.persist(thumbnail2);
        em.persist(thumbnail3);

        Game game = Game.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .user(user)
                .lastActivity(LocalDateTime.now())
                .build();

        em.persist(game);

        // When
        Optional<Thumbnail> result = underTest.findThumbnailNotInGameWithClosestRating(
                game.getId(),
                user.getId(),
                BigDecimal.valueOf(1400),
                thumbnail1.getId(),
                rating1.getPoints()
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(thumbnail2);
    }

    private static Round createRound(Game game, Thumbnail thumbnail1, Thumbnail thumbnail2) {
        return Round.builder()
                .game(game)
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .build();
    }

    private static User createUser() {
        return User.builder()
                .email("abc@abc.pl")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();
    }

    private static Thumbnail createThumbnail(String url, String ytVideoId, User user) {
        return Thumbnail.builder()
                .url(url)
                .youtubeVideoId(ytVideoId)
                .addedBy(user)
                .build();
    }

    private static Rating createRating(User user, Thumbnail thumbnail, double points) {
        return Rating.builder()
                .user(user)
                .thumbnail(thumbnail)
                .points(BigDecimal.valueOf(points))
                .build();
    }
}
