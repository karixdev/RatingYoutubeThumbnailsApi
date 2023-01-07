
package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeApiVideoListRequest {
    @JsonProperty("items")
    private List<ItemRequest> items = new ArrayList<>();
}
