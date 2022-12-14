package com.github.karixdev.youtubethumbnailranking.jwt;

import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.github.karixdev.youtubethumbnailranking.user.UserRole.ROLE_USER;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class JwtServiceIT {
    @Autowired
    JwtService underTest;

    UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        userPrincipal = new UserPrincipal(User.builder()
                .email("email@email.com")
                .username("username")
                .password("password")
                .userRole(ROLE_USER)
                .isEnabled(TRUE)
                .build());
    }

    @Test
    void shouldCreateValidToken() {
        String token = underTest.createToken(userPrincipal);
        assertThat(underTest.isTokenValid(token)).isTrue();
    }

    @Test
    void shouldGetEmailFromCreatedToken() {
        String token = underTest.createToken(userPrincipal);
        assertThat(underTest.getEmailFromToken(token))
                .isEqualTo("email@email.com");
    }
}
