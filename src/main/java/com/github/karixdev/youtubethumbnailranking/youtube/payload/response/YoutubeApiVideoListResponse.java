
package com.github.karixdev.youtubethumbnailranking.youtube.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YoutubeApiVideoListResponse {
    @JsonProperty("items")
    private List<Item> items = new ArrayList<>();
}
