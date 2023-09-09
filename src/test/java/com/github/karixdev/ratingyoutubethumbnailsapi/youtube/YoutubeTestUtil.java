package com.github.karixdev.ratingyoutubethumbnailsapi.youtube;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.*;

import java.util.Arrays;
import java.util.List;

public class YoutubeTestUtil {
    public static YoutubeApiVideosResponse createYtApiResponse(ItemResponse ...items) {
        return new YoutubeApiVideosResponse(Arrays.asList(items));
    }

    private static ThumbnailResponse createThumbnail(String url) {
        return new ThumbnailResponse(url, 100, 100);
    }

    private static ThumbnailsResponse createThumbnails() {
        return new ThumbnailsResponse(
                createThumbnail("default"),
                createThumbnail("medium"),
                createThumbnail("high"),
                createThumbnail("standard"),
                createThumbnail("max")
        );
    }

    private static SnippetResponse createSnippet() {
        return new SnippetResponse("title", "description", createThumbnails());
    }

    private static ItemResponse createItem() {
        return new ItemResponse("kind", "id", createSnippet());
    }

    public static YoutubeApiVideosResponse createYtApiResponse() {
       return new YoutubeApiVideosResponse(List.of(createItem()));
    }

    public static YoutubeVideoDTO createYoutubeVideDTO() {
        return new YoutubeVideoDTO(
                "id",
                "default",
                "medium",
                "high",
                "standard",
                "max"
        );
    }
}
