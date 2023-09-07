package com.github.karixdev.ratingyoutubethumbnailsapi.auth.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.LoginRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.RegisterRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.response.LoginResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt.JwtService;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.NewUserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserServiceApi userService;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {
        NewUserDTO newUserDTO = new NewUserDTO(
                request.email(),
                request.username(),
                request.password(),
                UserRole.USER
        );

        userService.create(newUserDTO);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        String jwt = jwtService.create(userDTO);

        return new LoginResponse(jwt);
    }
}
