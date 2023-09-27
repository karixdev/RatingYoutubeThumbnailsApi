package com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.rating;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RatingDTO(
        UUID id,
        UUID userId,
        UUID videoId,
        BigDecimal points
) {}