package com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResultRequest {
    @JsonProperty("winner_id")
    @NotNull
    private Long winnerId;
}
