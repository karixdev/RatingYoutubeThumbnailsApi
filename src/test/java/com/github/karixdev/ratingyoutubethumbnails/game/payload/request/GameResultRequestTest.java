package com.github.karixdev.ratingyoutubethumbnails.game.payload.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class GameResultRequestTest {
    @Autowired
    JacksonTester<GameResultRequest> jTester;

    @Test
    void testDeserialize() throws IOException {
        String payload = """
                {
                    "winner_id": 1
                }
                """;

        GameResultRequest result = jTester.parseObject(payload);

        assertThat(result.getWinnerId()).isEqualTo(1L);
    }
}
