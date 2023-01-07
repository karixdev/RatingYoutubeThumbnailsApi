package com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThumbnailRequest {
    @JsonProperty("youtube_video_id")
    @NotBlank
    @Size(min = 5)
    private String youtubeVideoId;
}
