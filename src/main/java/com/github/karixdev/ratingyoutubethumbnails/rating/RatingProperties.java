package com.github.karixdev.ratingyoutubethumbnails.rating;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Getter
@Service
public class RatingProperties {
    private final BigDecimal basePoints;
    private final Integer kParameter;

    public RatingProperties(
            @Value("${rating.base-points}") BigDecimal basePoints,
            @Value("${rating.k-parameter}") Integer kParameter
    ) {
        this.basePoints = basePoints;
        this.kParameter = kParameter;
    }
}
