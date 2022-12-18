package com.github.karixdev.youtubethumbnailranking.youtube;

import com.github.karixdev.youtubethumbnailranking.youtube.exception.YoutubeApiEmptyResponseException;
import com.github.karixdev.youtubethumbnailranking.youtube.exception.YoutubeVideoNotFoundException;
import com.github.karixdev.youtubethumbnailranking.youtube.payload.response.Item;
import com.github.karixdev.youtubethumbnailranking.youtube.payload.response.YoutubeApiVideoListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
@Service
public class YoutubeVideoService {
    private final YoutubeApiService apiService;
    private final WebClient webClient;

    public Item getVideoDetails(String id) {
        MultiValueMap<String, String> queryParams =
                new LinkedMultiValueMap<>();

        queryParams.add("id", id);
        queryParams.add("part", "snippet");
        queryParams.add("maxResults", "1");

        String uri = apiService.createUri("/videos", queryParams);

        Optional<YoutubeApiVideoListResponse> optionalResponse = webClient.get().uri(uri)
                .accept(APPLICATION_JSON)
                .retrieve()
                .bodyToMono(YoutubeApiVideoListResponse.class)
                .blockOptional();

        YoutubeApiVideoListResponse response = optionalResponse.orElseThrow(() -> {
            throw new YoutubeApiEmptyResponseException();
        });

        List<Item> items = response.getItems();
        if (items.isEmpty()) {
            throw new YoutubeVideoNotFoundException();
        }

        return items.get(0);
    }
}
