package com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.Rating;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThumbnailTest {
    @Test
    void GivenRating_WhenRating_ThenRatingIsAdded() {
        // Given
        Thumbnail underTest = Thumbnail.builder().build();
        Rating rating = Rating.builder().id(1L).build();

        // When
        underTest.addRating(rating);

        // Then
        assertThat(underTest.getRatings()).containsExactly(rating);
    }
}