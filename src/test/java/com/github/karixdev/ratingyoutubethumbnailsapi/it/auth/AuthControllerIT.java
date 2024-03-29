package com.github.karixdev.ratingyoutubethumbnailsapi.it.auth;

import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.response.SignInResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.EmailVerificationToken;
import com.github.karixdev.ratingyoutubethumbnailsapi.emailverification.EmailVerificationTokenRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.jwt.JwtService;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.mail.internet.MimeMessage;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthControllerIT extends ContainersEnvironment {
    @Autowired
    WebTestClient webClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailVerificationTokenRepository tokenRepository;

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @RegisterExtension
    static GreenMailExtension greenMail =
            new GreenMailExtension(ServerSetupTest.SMTP)
                    .withConfiguration(GreenMailConfiguration.aConfig()
                            .withUser("greenmail-user", "greenmail-password"))
                    .withPerMethodLifecycle(false);

    @AfterEach
    void tearDown() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldNotRegisterUserWhenProvidedNotAvailableEmail() {
        userService.createUser(
                "email@email.com",
                "username",
                "password",
                UserRole.ROLE_USER,
                Boolean.FALSE
        );

        String payload = """
                {
                    "email": "email@email.com",
                    "username": "not-taken-username",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertThat(userRepository.findAll().size())
                .isEqualTo(1);
    }

    @Test
    void shouldNotRegisterUserWhenProvidedNotAvailableUsername() {
        userService.createUser(
                "email@email.com",
                "username",
                "password",
                UserRole.ROLE_USER,
                Boolean.FALSE
        );

        String payload = """
                {
                    "email": "not-taken-email@email.com",
                    "username": "username",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        assertThat(userRepository.findAll().size())
                .isEqualTo(1);
        assertThat(tokenRepository.findAll())
                .isEmpty();
    }

    @Test
    void shouldRegisterUserAndCreateEmailVerificationToken() {
        String payload = """
                {
                    "email": "email@email.com",
                    "username": "username",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("success");

        assertThat(userRepository.findAll()).isNotEmpty();

        User user = userRepository.findAll().get(0);

        assertThat(user.getUserRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(user.getEmail()).isEqualTo("email@email.com");
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getIsEnabled()).isEqualTo(Boolean.FALSE);

        assertThat(tokenRepository.findAll().size())
                .isEqualTo(1);

        EmailVerificationToken token = tokenRepository.findAll().get(0);

        assertThat(token.getUser()).isEqualTo(user);

        await().atMost(2, SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

            assertThat(receivedMessages).hasSize(1);

            MimeMessage receivedMessage = receivedMessages[0];

            assertThat(receivedMessage.getSubject()).isEqualTo("Verify your email");
            assertThat(receivedMessage.getFrom()).hasSize(1);

            String from = receivedMessage.getFrom()[0].toString();
            assertThat(from).isEqualTo("test@youtube-thumbnail-ranking.com");

            assertThat(receivedMessage.getAllRecipients()).hasSize(1);

            String recipient = receivedMessage.getAllRecipients()[0].toString();
            assertThat(recipient).isEqualTo("email@email.com");
        });
    }

    @Test
    void shouldNotSignInNotEnabledUser() {
        userService.createUser(
                "email@email.com",
                "username",
                "password",
                UserRole.ROLE_USER,
                Boolean.FALSE
        );

        String payload = """
                {
                    "email": "email@email.com",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldNotSignInNotExistingUser() {
        String payload = """
                {
                    "email": "i-do-not-exist@email.com",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldNotSignInUserGivenInvalidCredentials() {
        userService.createUser(
                "email@email.com",
                "username",
                "password",
                UserRole.ROLE_USER,
                Boolean.TRUE
        );

        String payload = """
                {
                    "email": "email@email.pl",
                    "password": "password"
                }
                """;

        webClient.post().uri("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldSignInUserWithValidCredentials() {
        userService.createUser(
                "email@email.com",
                "username",
                "password",
                UserRole.ROLE_USER,
                Boolean.TRUE
        );

        String payload = """
                {
                    "email": "email@email.com",
                    "password": "password"
                }
                """;

        var response = webClient.post().uri("/api/v1/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SignInResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();

        assertThat(response.getUserResponse().getEmail()).isEqualTo("email@email.com");
        assertThat(response.getUserResponse().getUsername()).isEqualTo("username");
        assertThat(response.getUserResponse().getIsEnabled()).isEqualTo(Boolean.TRUE);
        assertThat(response.getUserResponse().getUserRole()).isEqualTo(UserRole.ROLE_USER);

        assertThat(jwtService.isTokenValid(response.getAccessToken())).isTrue();
    }
}
