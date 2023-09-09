package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.client;

import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.YoutubeApiVideosResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface YoutubeApiClient {
    @GetExchange("/videos?part=snippet&maxResults=1")
    YoutubeApiVideosResponse findMovies(
            @RequestParam("id") String id,
            @RequestParam("key") String key

    );
}
