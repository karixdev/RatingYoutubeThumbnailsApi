package com.github.karixdev.ratingyoutubethumbnails.rating.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    @JsonProperty("global_rating_points")
    private BigDecimal globalRatingPoints;

    @JsonProperty("user_rating_points")
    private BigDecimal userRatingPoints;
}
