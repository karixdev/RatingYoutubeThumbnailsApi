package com.github.karixdev.ratingyoutubethumbnailsapi.shared.game;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record GameDTO(
    UUID id,
    UUID userId,
    LocalDateTime lastActivity,
    Set<GameRoundDTO> rounds
) {}
