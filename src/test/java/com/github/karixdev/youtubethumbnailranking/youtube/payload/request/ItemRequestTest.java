package com.github.karixdev.youtubethumbnailranking.youtube.payload.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestTest {
    @Autowired
    JacksonTester<ItemRequest> jTester;

    @Test
    void testDeserialize() throws IOException {
        String payload = """
                {
                    "kind": "kind",
                    "id": "id",
                    "snippet": {
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
                }
                """;

        ItemRequest result = jTester.parseObject(payload);

        assertThat(result.getId()).isEqualTo("id");
        assertThat(result.getKind()).isEqualTo("kind");

        assertThat(result.getSnippet().getTitle()).isEqualTo("title");
        assertThat(result.getSnippet().getDescription()).isEqualTo("description");

        assertThat(result.getSnippet().getThumbnails().getMaxres().getUrl()).isEqualTo("url-maxres");
        assertThat(result.getSnippet().getThumbnails().getMaxres().getHeight()).isEqualTo(105);
        assertThat(result.getSnippet().getThumbnails().getMaxres().getWidth()).isEqualTo(105);
    }
}
