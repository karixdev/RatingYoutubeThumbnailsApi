package com.github.karixdev.youtubethumbnailranking.emailverification;

import com.github.karixdev.youtubethumbnailranking.email.EmailService;
import com.github.karixdev.youtubethumbnailranking.user.User;
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

}
