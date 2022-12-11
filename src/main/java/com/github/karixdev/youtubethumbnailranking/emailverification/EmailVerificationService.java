package com.github.karixdev.youtubethumbnailranking.emailverification;

import com.github.karixdev.youtubethumbnailranking.email.EmailService;
import com.github.karixdev.youtubethumbnailranking.emailverification.exception.EmailAlreadyVerifiedException;
import com.github.karixdev.youtubethumbnailranking.emailverification.exception.EmailVerificationTokenExpiredException;
import com.github.karixdev.youtubethumbnailranking.shared.exception.ResourceNotFoundException;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationTokenRepository tokenRepository;
    private final Clock clock;
    private final EmailService emailService;
    private final UserService userService;
    @Value("${email-verification.expiration-hours}")
    private Integer tokenExpirationHours;

    @Transactional
    public EmailVerificationToken createToken(User user) {
        LocalDateTime now = LocalDateTime.now(clock);
        String uuid = UUID.randomUUID().toString();

        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(uuid)
                .user(user)
                .createdAt(now)
                .expiresAt(now.plusHours(tokenExpirationHours))
                .build();

        return tokenRepository.save(token);
    }

    public void sendEmailWithVerificationLink(EmailVerificationToken token) {
        String link = String.format("http://localhost:8080/api/v1/verify/%s", token.getToken());

        Map<String, Object> variables = Map.of(
                "username", token.getUser().getUsername(),
                "verificationLink", link
        );
        String body = emailService.getMailTemplate("email-verification.html", variables);

        emailService.sendEmailToUser(token.getUser().getEmail(), "Verify your email", body);
    }

    public void setTokenExpirationHours(Integer expirationHours) {
        this.tokenExpirationHours = expirationHours;
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
}
