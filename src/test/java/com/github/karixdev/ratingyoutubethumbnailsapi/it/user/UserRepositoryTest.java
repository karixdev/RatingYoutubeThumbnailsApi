package com.github.karixdev.ratingyoutubethumbnailsapi.it.user;

import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest extends ContainersEnvironment {
    @Autowired
    UserRepository underTest;

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

        em.persistAndFlush(user);
    }

    @Test
    void GivenExistingUserEmail_WhenFindByEmail_ThenReturnsOptionalWithExpectedUser() {
        // Given
        String email = user.getEmail();

        // When
        Optional<User> result = underTest.findByEmail(email);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    void GivenNotExistingUserEmail_WhenFindByEmail_ThenReturnsEmptyOptional() {
        // Given
        String email = "email@email.com";

        // When
        Optional<User> result = underTest.findByEmail(email);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenExistingUserUsername_WhenFindByUsername_ThenReturnsOptionalWithExpectedUser() {
        // Given
        String username = user.getUsername();

        // When
        Optional<User> result = underTest.findByUsername(username);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    void GivenNotExistingUserUsername_WhenFindByUsername_ThenReturnsEmptyOptional() {
        // Given
        String username = "i-do-not-exist";

        // When
        Optional<User> result = underTest.findByUsername(username);

        // Then
        assertThat(result).isEmpty();
    }
}
