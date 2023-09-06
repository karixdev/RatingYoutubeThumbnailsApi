package com.github.karixdev.ratingyoutubethumbnailsapi.auth.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.RegisterRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.NewUserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserServiceApi userService;

    public void register(RegisterRequest request) {
        NewUserDTO newUserDTO = new NewUserDTO(
                request.email(),
                request.username(),
                request.password(),
                UserRole.USER
        );

        userService.create(newUserDTO);
    }
}
