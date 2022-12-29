package com.github.karixdev.youtubethumbnailranking.rating.payload.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RatingResponseTest {
    @Autowired
    JacksonTester<RatingResponse> jTester;

    @Test
    void testSerialization() throws IOException {
        RatingResponse payload = new RatingResponse(
                new BigDecimal(1500),
                new BigDecimal(1600)
        );

        var result = jTester.write(payload);

        assertThat(result).hasJsonPath("$.global_rating_points");
        assertThat(result).extractingJsonPathValue("$.global_rating_points")
                .isEqualTo(1500);

        assertThat(result).hasJsonPath("$.user_rating_points");
        assertThat(result).extractingJsonPathValue("$.user_rating_points")
                .isEqualTo(1600);
    }
}
