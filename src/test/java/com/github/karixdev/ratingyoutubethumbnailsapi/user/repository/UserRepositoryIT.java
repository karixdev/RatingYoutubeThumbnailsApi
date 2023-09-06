package com.github.karixdev.ratingyoutubethumbnailsapi.user.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class UserRepositoryIT extends ContainersEnvironment {
    @Autowired
    UserRepository underTest;

    @Autowired
    TestEntityManager em;

    @Test
    void GivenEmailThatIsNotInDB_WhenFindByEmail_ThenReturnsOptionalWithCorrectUser() {
        // Given
        String email = "email@email.com";
        em.persist(createUser("email2@email.com", "username2"));

        // When
        Optional<User> result = underTest.findByEmail(email);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenEmailThatIsAlreadyInDB_WhenFindByEmail_ThenReturnsOptionalWithCorrectUser() {
        // Given
        String email = "email@email.com";
        User user = em.persist(createUser(email, "username"));
        em.persist(createUser("email2@email.com", "username2"));

        // When
        Optional<User> result = underTest.findByEmail(email);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    void GivenUsernameThatIsNotInDB_WhenFindByUsername_ThenReturnsOptionalWithCorrectUser() {
        // Given
        String username = "username";
        em.persist(createUser("email2@email.com", "username2"));

        // When
        Optional<User> result = underTest.findByUsername(username);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenUsernameThatIsAlreadyInDB_WhenFindByUsername_ThenReturnsOptionalWithCorrectUser() {
        // Given
        String username = "username";
        User user = em.persist(createUser("email@email.com", username));
        em.persist(createUser("email2@email.com", "username2"));

        // When
        Optional<User> result = underTest.findByUsername(username);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(user);
    }

    private static User createUser(String email, String username) {
        return User.builder()
                .email(email)
                .username(username)
                .password("password")
                .role(UserRole.USER)
                .build();
    }
}