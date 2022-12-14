package com.github.karixdev.youtubethumbnailranking.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karixdev.youtubethumbnailranking.auth.payload.request.RegisterRequest;
import com.github.karixdev.youtubethumbnailranking.auth.payload.request.SignInRequest;
import com.github.karixdev.youtubethumbnailranking.auth.payload.response.SignInResponse;
import com.github.karixdev.youtubethumbnailranking.user.exception.EmailNotAvailableException;
import com.github.karixdev.youtubethumbnailranking.user.exception.UsernameNotAvailableException;
import com.github.karixdev.youtubethumbnailranking.user.payload.repsonse.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.karixdev.youtubethumbnailranking.user.UserRole.ROLE_USER;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {AuthController.class},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
public class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthService authService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void GivenInvalidCredentials_WhenRegister_ThenResponsesWithBadRequestStatus() throws Exception {
        RegisterRequest payload =
                new RegisterRequest("abc", "abc", "abc");
        String content = mapper.writeValueAsString(payload);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GivenTakenEmail_WhenRegister_ThenResponsesWithConflictStatus() throws Exception {
        RegisterRequest payload =
                new RegisterRequest("taken@email.com", "username", "password");
        String content = mapper.writeValueAsString(payload);

        doThrow(new EmailNotAvailableException())
                .when(authService)
                .registerNewUser(any());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict());
    }

    @Test
    void GivenTakenUsername_WhenRegister_ThenResponsesWithConflictStatus() throws Exception {
        RegisterRequest payload =
                new RegisterRequest("available@email.com", "taken-username", "password");
        String content = mapper.writeValueAsString(payload);

        doThrow(new UsernameNotAvailableException())
                .when(authService)
                .registerNewUser(any());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict());
    }

    @Test
    void GivenInvalidCredentials_WhenSignIn_ThenResponsesWithBadRequestStatus() throws Exception {
        SignInRequest payload = new SignInRequest("abc", "abc");

        String content = mapper.writeValueAsString(payload);

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GivenValidCredentials_WhenSignIn_ThenResponsesWithCorrectSingInResponse() throws Exception {
        SignInRequest payload =
                new SignInRequest("email@email.com", "password");

        String content = mapper.writeValueAsString(payload);

        when(authService.signIn(any()))
                .thenReturn(new SignInResponse(
                        "token",
                        new UserResponse(
                                "email@email.com",
                                "username",
                                ROLE_USER,
                                TRUE
                        )
                ));

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }
}
