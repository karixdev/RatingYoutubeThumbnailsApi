package com.github.karixdev.ratingyoutubethumbnailsapi.rating.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.entity.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class RatingRepositoryIT extends ContainersEnvironment {

    @Autowired
    RatingRepository underTest;

    @Autowired
    TestEntityManager em;

    @Test
    void GivenUserIdAndVideoId_WhenFindByUserIdAndVideoId_ThenReturnsOptionalWithCorrectRating() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID videoId = UUID.randomUUID();

        Rating rating = em.persist(TestUtils.createRating(userId, videoId, new BigDecimal("1")));

        em.persist(TestUtils.createRating(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("1")));
        em.persist(TestUtils.createRating(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("2")));

        // When
        Optional<Rating> result = underTest.findByUserIdAndVideoId(userId, videoId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(rating);
    }

    @Test
    void GivenUserId_WhenFindByUserId_ThenReturnsListUserRatings() {
        // Given
        UUID userId = UUID.randomUUID();

        Rating rating1 = em.persist(TestUtils.createRating(userId, UUID.randomUUID(), new BigDecimal("1")));
        Rating rating2 = em.persist(TestUtils.createRating(userId, UUID.randomUUID(), new BigDecimal("1")));

        em.persist(TestUtils.createRating(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("1")));

        // When
        List<Rating> result = underTest.findByUserId(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(rating1, rating2);
    }

}