package com.github.karixdev.youtubethumbnailranking.game.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResultRequest {
    @JsonProperty("winner_id")
    @NotNull
    private Long winnerId;
}
