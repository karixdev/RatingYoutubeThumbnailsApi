package com.github.karixdev.ratingyoutubethumbnailsapi.emailverification;

import com.github.karixdev.ratingyoutubethumbnailsapi.email.EmailServiceProvider;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.exception.EmailAlreadyVerifiedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.exception.EmailVerificationTokenExpiredException;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.exception.TooManyEmailVerificationTokensException;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.payload.request.ResendEmailVerificationTokenRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationTokenRepository tokenRepository;
    private final Clock clock;
    private final UserService userService;
    private final EmailVerificationProperties properties;
    private final EmailServiceProvider emailServiceProvider;

    private final String messageTemplate = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verify your email</title>
            </head>
            <body>
                <div class="container">
                    <h1>Hi %s!</h1>
                    <p>Here's your token: %s - it only lasts for 24 hours.</p>
                </div>
            </body>
            </html>
            """;

    @Transactional
    public EmailVerificationToken createToken(User user) {
        LocalDateTime now = LocalDateTime.now(clock);
        String uuid = UUID.randomUUID().toString();

        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(uuid)
                .user(user)
                .createdAt(now)
                .expiresAt(now.plusHours(properties.getTokenExpirationHours()))
                .build();

        return tokenRepository.save(token);
    }

    public void sendEmailWithVerificationLink(EmailVerificationToken token) {
        String body = messageTemplate.formatted(
                token.getUser().getUsername(),
                token.getToken()
        );

        emailServiceProvider.sendEmail(token.getUser().getEmail(), "Verify your email", body);
    }

    @Transactional
    public SuccessResponse verify(String token) {
        EmailVerificationToken emailVerificationToken =
                tokenRepository.findByToken(token).orElseThrow(() -> {
                    throw new ResourceNotFoundException(
                            "Email verification token not found"
                    );
                });

        LocalDateTime now = LocalDateTime.now(clock);

        if (emailVerificationToken.getUser().getIsEnabled()) {
            throw new EmailAlreadyVerifiedException();
        }

        if (!now.isBefore(emailVerificationToken.getExpiresAt())) {
            throw new EmailVerificationTokenExpiredException();
        }

        userService.enableUser(emailVerificationToken.getUser());

        emailVerificationToken.setConfirmedAt(now);
        tokenRepository.save(emailVerificationToken);

        return new SuccessResponse();
    }

    @Transactional
    public SuccessResponse resend(ResendEmailVerificationTokenRequest payload) {
        User user = userService.findByEmail(payload.getEmail());

        if (user.getIsEnabled()) {
            throw new EmailAlreadyVerifiedException();
        }

        if (!canUserCreateNewEmailVerificationToken(user)) {
            throw new TooManyEmailVerificationTokensException();
        }

        EmailVerificationToken newToken = createToken(user);
        sendEmailWithVerificationLink(newToken);

        return new SuccessResponse();
    }

    private boolean canUserCreateNewEmailVerificationToken(User user) {
        List<EmailVerificationToken> userTokens =
                tokenRepository.findByUserOrderByCreatedAtDesc(user);

        if (userTokens.size() < properties.getMaxNumberOfMailsPerHour()) {
            return true;
        }

        EmailVerificationToken latest = userTokens.get(0);

        long hoursBetweenNowAndLatest = ChronoUnit.HOURS.between(
                latest.getCreatedAt(),
                LocalDateTime.now(clock)
        );

        if (hoursBetweenNowAndLatest > 1) {
            return true;
        }

        EmailVerificationToken oldest =
                userTokens.get(properties.getMaxNumberOfMailsPerHour() - 1);

        long hoursBetweenLatestAndOldest = ChronoUnit.HOURS.between(
                latest.getCreatedAt(), oldest.getCreatedAt());

        return hoursBetweenLatestAndOldest > 1;
    }
}
