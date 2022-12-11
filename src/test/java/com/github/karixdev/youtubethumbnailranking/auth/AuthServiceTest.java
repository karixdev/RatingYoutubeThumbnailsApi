package com.github.karixdev.youtubethumbnailranking.auth;

import com.github.karixdev.youtubethumbnailranking.auth.payload.request.RegisterRequest;
import com.github.karixdev.youtubethumbnailranking.emailverification.EmailVerificationService;
import com.github.karixdev.youtubethumbnailranking.emailverification.EmailVerificationToken;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import com.github.karixdev.youtubethumbnailranking.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    AuthService underTest;

    @Mock
    UserService userService;

    @Mock
    EmailVerificationService emailVerificationService;

    User user;

    EmailVerificationToken token;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .build();

        token = EmailVerificationToken.builder()
                .token("random")
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
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

        when(userService.createUser(any(), any(), any(), any(), any()))
                .thenReturn(user);

        when(emailVerificationService.createToken(any()))
                .thenReturn(token);

        doNothing().when(emailVerificationService)
                .sendEmailWithVerificationLink(any());

        // When
        SuccessResponse result = underTest.registerNewUser(payload);

        // Then
        assertThat(result.getMessage()).isEqualTo("success");

        verify(userService).createUser(any(), any(), any(), any(), any());
        verify(emailVerificationService).createToken(any());
        verify(emailVerificationService).sendEmailWithVerificationLink(any());
    }

}
