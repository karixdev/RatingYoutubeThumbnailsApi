
package com.github.karixdev.youtubethumbnailranking.youtube.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Thumbnails {
    @JsonProperty("default")
    private Default _default;
    @JsonProperty("medium")
    private Medium medium;
    @JsonProperty("high")
    private High high;
    @JsonProperty("standard")
    private Standard standard;
    @JsonProperty("maxres")
    private Maxres maxres;
}
