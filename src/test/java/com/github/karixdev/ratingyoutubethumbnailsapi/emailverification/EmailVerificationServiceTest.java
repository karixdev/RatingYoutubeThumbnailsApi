package com.github.karixdev.ratingyoutubethumbnailsapi.emailverification;

import com.github.karixdev.ratingyoutubethumbnailsapi.email.EmailServiceProvider;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.exception.EmailAlreadyVerifiedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.exception.EmailVerificationTokenExpiredException;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.exception.TooManyEmailVerificationTokensException;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.payload.request.ResendEmailVerificationTokenRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.template.TemplateProvider;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationServiceTest {
    @InjectMocks
    EmailVerificationService underTest;

    @Mock
    EmailVerificationTokenRepository repository;

    @Mock
    UserService userService;

    @Mock
    Clock clock;

    @Mock
    EmailServiceProvider emailServiceProvider;

    @Mock
    TemplateProvider templateProvider;

    @Mock
    EmailVerificationProperties properties;

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

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

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

        when(templateProvider.getTemplate(any(), any()))
                .thenReturn("template");

        doNothing().when(emailServiceProvider)
                .sendEmail(any(), any(), any());

        // When
        underTest.sendEmailWithVerificationLink(token);

        // Then
        verify(templateProvider).getTemplate(any(), any());
        verify(emailServiceProvider).sendEmail(any(), any(), any());
    }

    @Test
    void GivenNotExistingToken_WhenVerify_ThenThrowsResourceNotFoundException() {
        // Given
        String token = "i-do-not-exist";

        when(repository.findByToken(any()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.verify(token))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Email verification token not found");
    }

    @Test
    void GivenTokenForAlreadyVerifiedEmail_WhenVerify_ThenThrowsEmailAlreadyVerifiedException() {
        // Given
        String token = "random";

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        EmailVerificationToken eToken = EmailVerificationToken.builder()
                .token("random")
                .user(user)
                .createdAt(NOW.toLocalDateTime())
                .expiresAt(NOW.plusHours(24).toLocalDateTime())
                .build();

        when(repository.findByToken(any()))
                .thenReturn(Optional.of(eToken));

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        // When & Then
        assertThatThrownBy(() -> underTest.verify(token))
                .isInstanceOf(EmailAlreadyVerifiedException.class)
                .hasMessage("Email is already verified");
    }

    @Test
    void GivenExpiredToken_WhenVerify_ThenThrowsEmailVerificationTokenExpiredException() {
        // Given
        String token = "random";

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.FALSE)
                .build();

        EmailVerificationToken eToken = EmailVerificationToken.builder()
                .token("random")
                .user(user)
                .createdAt(NOW.minusHours(30).toLocalDateTime())
                .expiresAt(NOW.minusHours(6).toLocalDateTime())
                .build();

        when(repository.findByToken(any()))
                .thenReturn(Optional.of(eToken));

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        // When & Then
        assertThatThrownBy(() -> underTest.verify(token))
                .isInstanceOf(EmailVerificationTokenExpiredException.class)
                .hasMessage("Email verification token has expired");
    }

    @Test
    void GivenValidToken_WhenVerify_ThenReturnsSuccessResponseAndEnablesUserAndSetsConfirmedAt() {
        // Given
        String token = "random";

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.FALSE)
                .build();

        EmailVerificationToken eToken = EmailVerificationToken.builder()
                .token("random")
                .user(user)
                .createdAt(NOW.toLocalDateTime())
                .expiresAt(NOW.plusHours(24).toLocalDateTime())
                .build();

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        when(repository.findByToken(any()))
                .thenReturn(Optional.of(eToken));

        // When
        SuccessResponse result = underTest.verify(token);

        // Then
        assertThat(result.getMessage()).isEqualTo("success");
        assertThat(eToken.getConfirmedAt()).isNotNull();

        verify(userService).enableUser(any());
        verify(repository).save(any());
    }

    @Test
    void GivenUserWithTokensCountLessThanMaxNumberOfMailsPerHour_WhenResend_ThenCreatesNewTokenAndReturnsSuccessResponse() {
        // Given
        var payload = new ResendEmailVerificationTokenRequest("email@email.com");

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.FALSE)
                .build();

        when(userService.findByEmail(any()))
                .thenReturn(user);

        EmailVerificationToken token = EmailVerificationToken.builder()
                .token("random")
                .user(user)
                .createdAt(NOW.toLocalDateTime())
                .expiresAt(NOW.plusHours(24).toLocalDateTime())
                .build();

        when(repository.findByUserOrderByCreatedAtDesc(any()))
                .thenReturn(List.of(token));

        when(properties.getMaxNumberOfMailsPerHour())
                .thenReturn(5);

        when(repository.save(any()))
                .thenReturn(token);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        when(templateProvider.getTemplate(any(), any()))
                .thenReturn("template");

        doNothing().when(emailServiceProvider)
                .sendEmail(any(), any(), any());

        // When
        SuccessResponse result = underTest.resend(payload);

        // Then
        assertThat(result.getMessage()).isEqualTo("success");
        verify(repository).save(any());
    }

    @Test
    void GivenUserWithTokensCountBiggerThanMaxButCreatedMoreThanHourAgo_WhenResend_ThenCreatesNewTokenAndReturnsSuccessResponse() {
        // Given
        var payload = new ResendEmailVerificationTokenRequest("email@email.com");

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.FALSE)
                .build();

        when(userService.findByEmail(any()))
                .thenReturn(user);

        List<EmailVerificationToken> tokens = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            EmailVerificationToken token = EmailVerificationToken.builder()
                    .token("random")
                    .user(user)
                    .createdAt(NOW.minusHours(20).toLocalDateTime())
                    .expiresAt(NOW.minusHours(44).toLocalDateTime())
                    .build();

            tokens.add(token);
        }

        when(repository.findByUserOrderByCreatedAtDesc(any()))
                .thenReturn(tokens);

        when(properties.getMaxNumberOfMailsPerHour())
                .thenReturn(5);

        when(repository.save(any()))
                .thenReturn(EmailVerificationToken.builder()
                        .token("random")
                        .user(user)
                        .createdAt(NOW.toLocalDateTime())
                        .expiresAt(NOW.plusHours(24).toLocalDateTime())
                        .build());

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        when(templateProvider.getTemplate(any(), any()))
                .thenReturn("template");

        doNothing().when(emailServiceProvider)
                .sendEmail(any(), any(), any());

        // When
        SuccessResponse result = underTest.resend(payload);

        // Then
        assertThat(result.getMessage()).isEqualTo("success");
        verify(repository).save(any());
    }

    @Test
    void GivenAlreadyEnabledUser_WhenResend_ThenThrowsEmailAlreadyVerifiedException() {
        // Given
        var payload = new ResendEmailVerificationTokenRequest("email@email.com");

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        when(userService.findByEmail(any()))
                .thenReturn(user);

        // When & Then
        assertThatThrownBy(() -> underTest.resend(payload))
                .isInstanceOf(EmailAlreadyVerifiedException.class)
                .hasMessage("Email is already verified");
    }

    @Test
    void GivenUserWithTokensCountBiggerThanMaxButCreatedLessThanHourAgo_WhenResend_ThenThrowsTooManyEmailVerificationTokensException() {
        // Given
        var payload = new ResendEmailVerificationTokenRequest("email@email.com");

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.FALSE)
                .build();

        when(userService.findByEmail(any()))
                .thenReturn(user);

        List<EmailVerificationToken> tokens = new LinkedList<>();

        for (int i = 0; i < 5; i++) {
            EmailVerificationToken token = EmailVerificationToken.builder()
                    .token("random")
                    .user(user)
                    .createdAt(NOW.minusMinutes(20 - i).toLocalDateTime())
                    .expiresAt(NOW.plusHours(24).toLocalDateTime())
                    .build();

            tokens.add(token);
        }

        when(repository.findByUserOrderByCreatedAtDesc(any()))
                .thenReturn(tokens);

        when(properties.getMaxNumberOfMailsPerHour())
                .thenReturn(5);

        when(clock.getZone()).thenReturn(NOW.getZone());
        when(clock.instant()).thenReturn(NOW.toInstant());

        // When & Then
        assertThatThrownBy(() -> underTest.resend(payload))
                .isInstanceOf(TooManyEmailVerificationTokensException.class)
                .hasMessage("You have requested too many email verification tokens");
    }
}