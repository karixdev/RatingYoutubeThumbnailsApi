package com.github.karixdev.ratingyoutubethumbnailsapi.auth.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.RegisterRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.NewUserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks
    AuthService underTest;

    @Mock
    UserServiceApi userService;

    @Test
    void GivenRegisterRequest_WhenRegister_ThenCreatesNewUser() {
        // Given
        RegisterRequest request = new RegisterRequest(
                "email@email.com", "username", "password");

        NewUserDTO newUserDTO = new NewUserDTO(
                request.email(), request.username(), request.password(), UserRole.USER);

        // When
        underTest.register(request);

        // Then
        verify(userService).create(eq(newUserDTO));
    }
}