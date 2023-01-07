package com.github.karixdev.ratingyoutubethumbnailsapi.auth;

import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.request.RegisterRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.request.SignInRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.response.SignInResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.EmailVerificationService;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.EmailVerificationToken;
import com.github.karixdev.ratingyoutubethumbnailsapi.jwt.JwtService;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;

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
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtService jwtService;

    User user;
    EmailVerificationToken token;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
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

    @Test
    @WithMockUser("customUsername")
    void GivenSignInRequest_WhenSignIn_ThenReturnsCorrectSignInResponse() {
        // Given
        SignInRequest payload =
                new SignInRequest("email@email.com", "password");

        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        new UserPrincipal(user),
                        "email@email.com:password"
                ));

        when(jwtService.createToken(any()))
                .thenReturn("token");

        // When
        SignInResponse result = underTest.signIn(payload);

        // Then
        assertThat(result.getAccessToken()).isEqualTo("token");

        assertThat(result.getUserResponse().getIsEnabled()).isTrue();
        assertThat(result.getUserResponse().getUserRole())
                .isEqualTo(UserRole.ROLE_USER);
        assertThat(result.getUserResponse().getUsername())
                .isEqualTo("username");
        assertThat(result.getUserResponse().getEmail())
                .isEqualTo("email@email.com");
    }

}
