package com.github.karixdev.ratingyoutubethumbnails.youtube;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class YoutubeApiProperties {
    private final String baseUrl;
    private final String apiKey;

    public YoutubeApiProperties(
            @Value("${youtube-api.base-url}") String baseUrl,
            @Value("${youtube-api.key}") String apiKey
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }
}
