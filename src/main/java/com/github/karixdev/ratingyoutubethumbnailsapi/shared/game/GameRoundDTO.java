package com.github.karixdev.ratingyoutubethumbnailsapi.shared.game;

import java.util.UUID;

public record GameRoundDTO(
        UUID id,
        UUID firstVideoId,
        UUID secondVideoId
) {}
