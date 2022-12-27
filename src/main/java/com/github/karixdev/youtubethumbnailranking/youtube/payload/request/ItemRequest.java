
package com.github.karixdev.youtubethumbnailranking.youtube.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @JsonProperty("kind")
    private String kind;
    @JsonProperty("id")
    private String id;
    @JsonProperty("snippet")
    private SnippetRequest snippet;
}
