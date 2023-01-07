package com.github.karixdev.ratingyoutubethumbnailsapi.rating;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class RatingPropertiesTest extends ContainersEnvironment {
    @Autowired
    RatingProperties underTest;

    @Test
    void shouldLoadBasePoints() {
        assertThat(underTest.getBasePoints())
                .isEqualTo(new BigDecimal(1400));
    }

    @Test
    void shouldLoadKParameter() {
        assertThat(underTest.getKParameter())
                .isEqualTo(32);
    }
}
