package com.github.karixdev.ratingyoutubethumbnailsapi.security;

import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import static com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole.ROLE_USER;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;

public class UserPrincipalTest {

    UserPrincipal underTest = new UserPrincipal(
            User.builder()
                    .email("email@email.com")
                    .password("password")
                    .username("username")
                    .userRole(ROLE_USER)
                    .isEnabled(TRUE)
                    .build()
    );

    @Test
    void shouldReturnCorrectAuthorities() {
        assertThat(underTest.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .hasSize(1)
                .contains("ROLE_USER");
    }

    @Test
    void shouldReturnCorrectPassword() {
        assertThat(underTest.getPassword()).isEqualTo("password");
    }

    @Test
    void shouldReturnCorrectUsername() {
        assertThat(underTest.getUsername()).isEqualTo("email@email.com");
    }

    @Test
    void shouldReturnCorrectIsAccountNonExpired() {
        assertThat(underTest.isAccountNonExpired()).isTrue();
    }

    @Test
    void shouldReturnCorrectIsAccountNonLocked() {
        assertThat(underTest.isAccountNonLocked()).isTrue();
    }

    @Test
    void shouldReturnCorrectIsCredentialsNonExpired() {
        assertThat(underTest.isCredentialsNonExpired()).isTrue();
    }

    @Test
    void shouldReturnCorrectIsEnabled() {
        assertThat(underTest.isEnabled()).isTrue();
    }
}
