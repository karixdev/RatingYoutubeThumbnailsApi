package com.github.karixdev.ratingyoutubethumbnails.rating;

import com.github.karixdev.ratingyoutubethumbnails.rating.payload.response.RatingResponse;
import com.github.karixdev.ratingyoutubethumbnails.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = RatingController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class RatingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    RatingService ratingService;

    @Test
    void GivenNotExistingYoutubeVideoId_WhenGetThumbnailAveragePoints_ThenRespondsWithNotFoundStatus() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(ratingService)
                .getThumbnailAveragePoints(any(), any());

        mockMvc.perform(get("/api/v1/rating/i-do-not-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    void GivenYoutubeVideoIdAndUserPrincipal_WhenGetThumbnailAveragePoints_ThenRespondsWithNotFoundStatus() throws Exception {
        when(ratingService.getThumbnailAveragePoints(any(), any()))
                .thenReturn(new RatingResponse(
                        new BigDecimal("1400.0"),
                        new BigDecimal("1500.0")
                ));

        mockMvc.perform(get("/api/v1/rating/i-do-not-exist"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.global_rating_points").value(new BigDecimal("1400.0")),
                        jsonPath("$.user_rating_points").value(new BigDecimal("1500.0"))
                );
    }

    @Test
    void GivenYoutubeVideoIdAndNullUserPrincipal_WhenGetThumbnailAveragePoints_ThenRespondsWithNotFoundStatus() throws Exception {
        when(ratingService.getThumbnailAveragePoints(any(), any()))
                .thenReturn(new RatingResponse(
                        new BigDecimal("1400.0"),
                        null
                ));

        mockMvc.perform(get("/api/v1/rating/i-do-not-exist"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.global_rating_points").value(new BigDecimal("1400.0")),
                        jsonPath("$.user_rating_points").isEmpty()
                );
    }
}
