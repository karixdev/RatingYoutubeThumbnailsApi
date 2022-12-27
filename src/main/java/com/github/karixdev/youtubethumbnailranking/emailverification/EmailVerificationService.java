package com.github.karixdev.youtubethumbnailranking.emailverification;

import com.github.karixdev.youtubethumbnailranking.email.EmailService;
import com.github.karixdev.youtubethumbnailranking.emailverification.exception.EmailAlreadyVerifiedException;
import com.github.karixdev.youtubethumbnailranking.emailverification.exception.EmailVerificationTokenExpiredException;
import com.github.karixdev.youtubethumbnailranking.emailverification.exception.TooManyEmailVerificationTokensException;
import com.github.karixdev.youtubethumbnailranking.emailverification.payload.request.ResendEmailVerificationTokenRequest;
import com.github.karixdev.youtubethumbnailranking.shared.exception.ResourceNotFoundException;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationTokenRepository tokenRepository;
    private final Clock clock;
    private final EmailService emailService;
    private final UserService userService;
    private final EmailVerificationProperties properties;

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
        Map<String, Object> variables = Map.of(
                "username", token.getUser().getUsername(),
                "token", token.getToken()
        );

        String body = emailService.getMailTemplate("email-verification.html", variables);

        emailService.sendEmailToUser(token.getUser().getEmail(), "Verify your email", body);
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
