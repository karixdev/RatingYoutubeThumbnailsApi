package com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user;

import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;

public record NewUserDTO(
        String email,
        String username,
        String password,
        UserRole role
) {}
