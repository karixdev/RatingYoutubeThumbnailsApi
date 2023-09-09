package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.client.YoutubeApiClient;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.mapper.YoutubeVideoMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.YoutubeApiVideosResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class YoutubeService implements YoutubeServiceApi {
    private final YoutubeApiClient apiClient;
    private final YoutubeVideoMapper mapper;

    private final String key;

    public YoutubeService(YoutubeApiClient apiClient, YoutubeVideoMapper mapper, @Value("${youtube-api.key}") String key) {
        this.apiClient = apiClient;
        this.mapper = mapper;
        this.key = key;
    }

    @Override
    public Optional<YoutubeVideoDTO> findYoutubeMovieById(String id) {
        YoutubeApiVideosResponse response = apiClient.findMovies(id, key);

        if (response == null || response.items().size() != 1) {
            return Optional.empty();
        }

        return Optional.of(response.items().get(0)).map(mapper::itemToDTO);
    }
}
