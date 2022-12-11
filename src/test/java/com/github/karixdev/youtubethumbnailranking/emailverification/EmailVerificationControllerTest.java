package com.github.karixdev.youtubethumbnailranking.emailverification;

import com.github.karixdev.youtubethumbnailranking.emailverification.exception.EmailAlreadyVerifiedException;
import com.github.karixdev.youtubethumbnailranking.emailverification.exception.EmailVerificationTokenExpiredException;
import com.github.karixdev.youtubethumbnailranking.shared.exception.ResourceNotFoundException;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = {EmailVerificationController.class},
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
public class EmailVerificationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmailVerificationService emailVerificationService;

    @Test
    void GivenNotExistingToken_WhenVerify_ThenResponsesWith404() throws Exception {
        String token = "i-do-not-exist";

        doThrow(new ResourceNotFoundException("Email verification token not found"))
                .when(emailVerificationService)
                .verify(any());

        mockMvc.perform(post("/api/v1/email-verification/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void GivenAlreadyVerifiedToken_WhenVerify_ThenResponsesWith400() throws Exception {
        String token = "i-do-not-exist";

        doThrow(new EmailVerificationTokenExpiredException())
                .when(emailVerificationService)
                .verify(any());

        mockMvc.perform(post("/api/v1/email-verification/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GivenExpiredToken_WhenVerify_ThenResponsesWith400() throws Exception {
        String token = "i-do-not-exist";

        doThrow(new EmailAlreadyVerifiedException())
                .when(emailVerificationService)
                .verify(any());

        mockMvc.perform(post("/api/v1/email-verification/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GivenValidToken_WhenVerify_ThenResponsesWith200AndSuccessMessage() throws Exception {
        String token = "i-exist";

        when(emailVerificationService.verify(any()))
                .thenReturn(new SuccessResponse());

        mockMvc.perform(post("/api/v1/email-verification/" + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

}
