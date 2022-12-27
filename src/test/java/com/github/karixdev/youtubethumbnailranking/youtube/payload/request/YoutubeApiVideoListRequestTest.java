package com.github.karixdev.youtubethumbnailranking.youtube.payload.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class YoutubeApiVideoListRequestTest {
    @Autowired
    JacksonTester<YoutubeApiVideoListRequest> jTester;

    @Test
    void testDeserialize() throws IOException {
        String payload = """
                {
                    "items": [
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
                    ]
                }
                """;

        YoutubeApiVideoListRequest result = jTester.parseObject(payload);

        assertThat(result.getItems()).hasSize(1);

        assertThat(result.getItems().get(0).getId()).isEqualTo("id");
        assertThat(result.getItems().get(0).getKind()).isEqualTo("kind");

        assertThat(result.getItems().get(0).getSnippet().getTitle()).isEqualTo("title");
        assertThat(result.getItems().get(0).getSnippet().getDescription()).isEqualTo("description");

        assertThat(result.getItems().get(0).getSnippet().getThumbnails().getMaxres().getUrl()).isEqualTo("url-maxres");
        assertThat(result.getItems().get(0).getSnippet().getThumbnails().getMaxres().getHeight()).isEqualTo(105);
        assertThat(result.getItems().get(0).getSnippet().getThumbnails().getMaxres().getWidth()).isEqualTo(105);
    }
}
