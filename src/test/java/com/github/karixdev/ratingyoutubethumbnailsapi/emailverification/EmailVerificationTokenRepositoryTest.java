package com.github.karixdev.ratingyoutubethumbnailsapi.emailverification;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EmailVerificationTokenRepositoryTest extends ContainersEnvironment {
    @Autowired
    EmailVerificationTokenRepository underTest;

    @Autowired
    TestEntityManager em;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("abc@abc.pl")
                .username("username")
                .password("secret-password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.FALSE)
                .build();

        em.persist(user);
    }

    @Test
    void GivenNonExistingToken_WhenFindByToken_ThenReturnsEmptyOptional() {
        // Given
        String token = "i-do-not-exist";

        EmailVerificationToken eToken = EmailVerificationToken.builder()
                .user(user)
                .token("token")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .build();

        em.persistAndFlush(eToken);

        // When
        var result = underTest.findByToken(token);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenExistingToken_WhenFindByToken_ThenReturnsOptionalWithCorrectEmailVerificationToken() {
        // Given
        String token = "token";

        EmailVerificationToken eToken = EmailVerificationToken.builder()
                .user(user)
                .token("token")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .build();

        em.persistAndFlush(eToken);

        // When
        var result = underTest.findByToken(token);

        // Then
        assertThat(result).isNotEmpty();

        var resultToken = result.get();

        assertThat(resultToken).isEqualTo(eToken);
    }

    @Test
    void GivenUser_WhenFindUserOrderByCreatedAtDesc_ThenReturnsProperlySortedList() {
        for (int i = 0; i < 2; i++) {
            EmailVerificationToken eToken = EmailVerificationToken.builder()
                    .user(user)
                    .token("token-" + i)
                    .expiresAt(LocalDateTime.now().plusHours(1 + i))
                    .createdAt(LocalDateTime.now())
                    .build();

            em.persist(eToken);
        }
        em.flush();

        // When
        List<EmailVerificationToken> eTokens =
                underTest.findByUserOrderByCreatedAtDesc(user);

        // Then
        assertThat(eTokens).hasSize(2);

        EmailVerificationToken latest = eTokens.get(0);
        EmailVerificationToken oldest = eTokens.get(1);

        assertThat(latest.getCreatedAt()).isAfter(oldest.getCreatedAt());
    }

}
