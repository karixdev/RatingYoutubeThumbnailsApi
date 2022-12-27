package com.github.karixdev.youtubethumbnailranking.thumbnail.payload.request;

import com.github.karixdev.youtubethumbnailranking.thumnail.payload.request.ThumbnailRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ThumbnailRequestTest {
    @Autowired
    JacksonTester<ThumbnailRequest> jTester;

    @Test
    void testDeserialize() throws IOException {
        String payload = """
                {
                    "youtube_video_id": "123456789"
                }
                """;

        ThumbnailRequest result = jTester.parseObject(payload);

        assertThat(result.getYoutubeVideoId())
                .isEqualTo("123456789");
    }
}
