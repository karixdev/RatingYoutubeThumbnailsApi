package com.github.karixdev.youtubethumbnailranking.youtube.payload.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ThumbnailsRequestTest {
    @Autowired
    JacksonTester<ThumbnailsRequest> jTester;

    @Test
    void testDeserialize() throws IOException {
        String payload = """
                {
                     "default": {
                        "url": "url-default",
                        "width": 101,
                        "height": 101
                     },
                     "medium": {
                        "url": "url-medium",
                        "width": 102,
                        "height": 102
                     },
                     "high": {
                        "url": "url-high",
                        "width": 103,
                        "height": 103
                     },
                     "standard": {
                        "url": "url-standard",
                        "width": 104,
                        "height": 104
                     },
                     "maxres": {
                        "url": "url-maxres",
                        "width": 105,
                        "height": 105
                     }
                }
                """;

        ThumbnailsRequest result = jTester.parseObject(payload);

        assertThat(result.get_default().getUrl()).isEqualTo("url-default");
        assertThat(result.get_default().getHeight()).isEqualTo(101);
        assertThat(result.get_default().getWidth()).isEqualTo(101);

        assertThat(result.getMedium().getUrl()).isEqualTo("url-medium");
        assertThat(result.getMedium().getHeight()).isEqualTo(102);
        assertThat(result.getMedium().getWidth()).isEqualTo(102);

        assertThat(result.getHigh().getUrl()).isEqualTo("url-high");
        assertThat(result.getHigh().getHeight()).isEqualTo(103);
        assertThat(result.getHigh().getWidth()).isEqualTo(103);

        assertThat(result.getStandard().getUrl()).isEqualTo("url-standard");
        assertThat(result.getStandard().getHeight()).isEqualTo(104);
        assertThat(result.getStandard().getWidth()).isEqualTo(104);

        assertThat(result.getMaxres().getUrl()).isEqualTo("url-maxres");
        assertThat(result.getMaxres().getHeight()).isEqualTo(105);
        assertThat(result.getMaxres().getWidth()).isEqualTo(105);
    }
}
