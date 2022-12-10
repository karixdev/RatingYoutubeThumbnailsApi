package com.github.karixdev.youtubethumbnailranking.auth;

import com.github.karixdev.youtubethumbnailranking.auth.payload.request.RegisterRequest;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import com.github.karixdev.youtubethumbnailranking.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    AuthService underTest;

    @Mock
    UserService userService;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .build();
    }

    @Test
    void GivenRegisterRequest_WhenRegisterNewUser_ThenReturnsSuccessResponse() {
        // Given
        RegisterRequest payload = RegisterRequest.builder()
                .password("password")
                .email("email@email.com")
                .username("username")
                .build();

        when(userService.createUser(any(), any(), any(), any()))
                .thenReturn(user);

        // When
        SuccessResponse result = underTest.registerNewUser(payload);

        // Then
        assertThat(result.getMessage()).isEqualTo("success");
        verify(userService).createUser(any(), any(), any(), any());
    }

}
