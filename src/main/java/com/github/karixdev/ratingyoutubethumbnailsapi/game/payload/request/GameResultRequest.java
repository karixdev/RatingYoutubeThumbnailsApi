package com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record GameResultRequest(
        @JsonProperty("winnerVideoId")
        UUID winnerVideoId
) {}
