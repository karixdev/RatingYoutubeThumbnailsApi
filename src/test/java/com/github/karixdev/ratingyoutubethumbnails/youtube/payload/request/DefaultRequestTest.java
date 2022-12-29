package com.github.karixdev.ratingyoutubethumbnails.youtube.payload.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DefaultRequestTest {
    @Autowired
    JacksonTester<DefaultRequest> jTester;

    @Test
    void testDeserialize() throws IOException {
        String payload = """
                {
                    "url": "url",
                    "width": 100,
                    "height": 100
                }
                """;

        DefaultRequest result = jTester.parseObject(payload);

        assertThat(result.getUrl()).isEqualTo("url");
        assertThat(result.getHeight()).isEqualTo(100);
        assertThat(result.getWidth()).isEqualTo(100);
    }
}
