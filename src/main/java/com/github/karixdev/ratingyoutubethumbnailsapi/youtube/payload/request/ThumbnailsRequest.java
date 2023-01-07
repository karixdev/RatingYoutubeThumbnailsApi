
package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailsRequest {
    @JsonProperty("default")
    private DefaultRequest _default;
    @JsonProperty("medium")
    private MediumRequest medium;
    @JsonProperty("high")
    private HighRequest high;
    @JsonProperty("standard")
    private StandardRequest standard;
    @JsonProperty("maxres")
    private MaxresRequest maxres;
}
