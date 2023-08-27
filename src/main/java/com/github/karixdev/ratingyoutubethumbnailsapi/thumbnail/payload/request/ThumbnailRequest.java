package com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThumbnailRequest {
    @JsonProperty("youtube_video_id")
    @NotBlank
    @Size(min = 5)
    private String youtubeVideoId;
}
