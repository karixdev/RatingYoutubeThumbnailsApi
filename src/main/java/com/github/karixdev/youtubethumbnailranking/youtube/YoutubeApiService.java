package com.github.karixdev.youtubethumbnailranking.youtube;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class YoutubeApiService {
    private final YoutubeApiProperties apiProperties;

    public String createUri(String endpoint, MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder
                .fromUriString(apiProperties.getBaseUrl())
                .path(endpoint)
                .queryParams(queryParams)
                .queryParam("key", apiProperties.getApiKey())
                .build().toUriString();
    }
}
