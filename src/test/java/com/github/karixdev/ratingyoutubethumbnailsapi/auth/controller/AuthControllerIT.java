package com.github.karixdev.ratingyoutubethumbnailsapi.auth.controller;


import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class AuthControllerIT extends ContainersEnvironment {
    @Autowired
    WebTestClient webClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldNotRegisterUserWithNotAvailableEmail() {
        userRepository.save(createUser("email@email.com", "username2", "password"));

        String body = """
                {
                    "email": "email@email.com",
                    "username": "username",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldNotRegisterUserWithNotAvailableUsername() {
        userRepository.save(createUser("email2@email.com", "username", "password"));

        String body = """
                {
                    "email": "email@email.com",
                    "username": "username",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void shouldRegisterUser() {
        String body = """
                {
                    "email": "email@email.com",
                    "username": "username",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNoContent();

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);

        User user = users.get(0);
        assertThat(user.getEmail()).isEqualTo("email@email.com");
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void shouldNotLoginUserGivenCredentialsWithInvalidEmail() {
        userRepository.save(createUser("email2@email.com", "username", "password"));

        String body = """
                {
                    "email": "email@email.com",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldNotLoginUserGivenCredentialsWithInvalidPassword() {
        userRepository.save(createUser("email@email.com", "username", "password2"));

        String body = """
                {
                    "email": "email@email.com",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldLoginUser() {
        userRepository.save(createUser("email@email.com", "username", "password"));

        String body = """
                {
                    "email": "email@email.com",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("accessToken").isNotEmpty();
    }

    private User createUser(String email, String username, String password) {
        return User.builder()
                .email(email)
                .username(username)
                .password(encoder.encode(password))
                .role(UserRole.USER)
                .build();
    }
}