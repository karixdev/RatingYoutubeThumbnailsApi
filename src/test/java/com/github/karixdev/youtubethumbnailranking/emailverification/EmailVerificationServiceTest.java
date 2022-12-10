package com.github.karixdev.youtubethumbnailranking.emailverification;

import com.github.karixdev.youtubethumbnailranking.email.EmailService;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTest {
    @InjectMocks
    EmailVerificationService underTest;

    @Mock
    EmailVerificationTokenRepository repository;

    @Mock
    Clock clock;

    @Mock
    EmailService emailService;

    private static final ZonedDateTime NOW = ZonedDateTime.of(
            2022,
            11,
            23,
            13,
            44,
            30,
            0,
            ZoneId.of("UTC+1")
    );

    @BeforeEach
    void setUp() {
        underTest.setTokenExpirationHours(24);
    }

    @Test
    void GivenUser_WhenCreateToken_ThenReturnsCorrectEmailVerificationToken() {
        // Given
        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.FALSE)
                .build();

        EmailVerificationToken token = EmailVerificationToken.builder()
                .token("random")
                .user(user)
                .createdAt(NOW.toLocalDateTime())
                .expiresAt(NOW.plusHours(24).toLocalDateTime())
                .build();

        when(repository.save(any()))
                .thenReturn(token);

        // When
        EmailVerificationToken result = underTest.createToken(user);

        // Then
        assertThat(result).isEqualTo(token);
    }

    @Test
    void GivenEmailVerificationToken_WhenSendEmailWithVerificationLink_ThenSendEmail() {
        // Given
        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.FALSE)
                .build();

        EmailVerificationToken token = EmailVerificationToken.builder()
                .token("random")
                .user(user)
                .createdAt(NOW.toLocalDateTime())
                .expiresAt(NOW.plusHours(24).toLocalDateTime())
                .build();

        when(emailService.getMailTemplate(any(), any()))
                .thenReturn("template");

        doNothing().when(emailService)
                .sendEmailToUser(any(), any(), any());

        // When
        underTest.sendEmailWithVerificationLink(token);

        // Then
        verify(emailService).getMailTemplate(any(), any());
        verify(emailService).sendEmailToUser(any(), any(), any());
    }
}
