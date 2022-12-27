package com.github.karixdev.youtubethumbnailranking.youtube.payload.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class SnippetRequestTest {
    @Autowired
    JacksonTester<SnippetRequest> jTester;

    @Test
    void testDeserialize() throws IOException {
        String payload = """
                {
                    "title": "title",
                    "description": "description",
                    "thumbnails": {
                        "maxres": {
                            "url": "url-maxres",
                            "width": 105,
                            "height": 105
                        }
                    }
                }
                """;

        SnippetRequest result = jTester.parseObject(payload);

        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getDescription()).isEqualTo("description");

        assertThat(result.getThumbnails().getMaxres().getUrl()).isEqualTo("url-maxres");
        assertThat(result.getThumbnails().getMaxres().getHeight()).isEqualTo(105);
        assertThat(result.getThumbnails().getMaxres().getWidth()).isEqualTo(105);
    }
}
