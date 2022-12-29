package com.github.karixdev.ratingyoutubethumbnails.jwt;

import com.github.karixdev.ratingyoutubethumbnails.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnails.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnails.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.github.karixdev.ratingyoutubethumbnails.user.UserRole.ROLE_USER;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class JwtServiceIT extends ContainersEnvironment {
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
