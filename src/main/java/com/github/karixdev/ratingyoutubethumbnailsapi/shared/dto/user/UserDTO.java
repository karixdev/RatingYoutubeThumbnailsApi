package com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user;

import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        String username,
        UserRole role,
        String password
) {}
