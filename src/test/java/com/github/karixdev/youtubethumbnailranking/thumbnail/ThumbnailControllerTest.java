package com.github.karixdev.youtubethumbnailranking.thumbnail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karixdev.youtubethumbnailranking.shared.exception.PermissionDeniedException;
import com.github.karixdev.youtubethumbnailranking.shared.exception.ResourceNotFoundException;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailController;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import com.github.karixdev.youtubethumbnailranking.thumnail.exception.ThumbnailAlreadyExistsException;
import com.github.karixdev.youtubethumbnailranking.thumnail.payload.request.ThumbnailRequest;
import com.github.karixdev.youtubethumbnailranking.thumnail.payload.response.ThumbnailResponse;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        controllers = ThumbnailController.class
)
public class ThumbnailControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    ThumbnailService thumbnailService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void WhenGivenInvalidYoutubeVideoId_WhenAdd_ThenRespondsWithBadRequestStatus() throws Exception {
        ThumbnailRequest payload = new ThumbnailRequest("123");
        String content = mapper.writeValueAsString(payload);

        mockMvc.perform(post("/api/v1/thumbnail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void WhenGivenYoutubeVideoIdThatAlreadyIsInDatabase_WhenAdd_ThenRespondsWithConflictStatus() throws Exception {
        ThumbnailRequest payload = new ThumbnailRequest("1234567");
        String content = mapper.writeValueAsString(payload);

        doThrow(ThumbnailAlreadyExistsException.class).when(thumbnailService)
                .add(any(), any());

        mockMvc.perform(post("/api/v1/thumbnail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict());
    }

    @Test
    void WhenGivenValidYoutubeVideoId_WhenAdd_ThenRespondsWithCorrectBodyAndOkCreatedStatus() throws Exception {
        ThumbnailRequest payload = new ThumbnailRequest("1234567");
        String content = mapper.writeValueAsString(payload);

        User user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        Thumbnail thumbnail = Thumbnail.builder()
                .id(1L)
                .addedBy(user)
                .url("thumbnail-url-1")
                .youtubeVideoId("youtube-id-1")
                .build();

        when(thumbnailService.add(any(), any()))
                .thenReturn(new ThumbnailResponse(thumbnail));

        mockMvc.perform(post("/api/v1/thumbnail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").value(1),
                        jsonPath("$.youtube_video_id").value("youtube-id-1"),
                        jsonPath("$.added_by.username").value("username")
                );
    }

    @Test
    void GivenNotExistingThumbnailId_WhenDelete_ThenRespondsWithNotFoundStatus() throws Exception {
        ThumbnailRequest payload = new ThumbnailRequest("1234567");
        String content = mapper.writeValueAsString(payload);

        doThrow(ResourceNotFoundException.class).when(thumbnailService)
                .delete(any(), any());

        mockMvc.perform(delete("/api/v1/thumbnail/1337")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound());
    }

    @Test
    void GivenValidThumbnailIdAndNotOwnerOfThumbnail_WhenDelete_ThenRespondsWithForbiddenStatus() throws Exception {
        ThumbnailRequest payload = new ThumbnailRequest("1234567");
        String content = mapper.writeValueAsString(payload);

        doThrow(PermissionDeniedException.class).when(thumbnailService)
                .delete(any(), any());

        mockMvc.perform(delete("/api/v1/thumbnail/1337")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    void GivenValidThumbnailId_WhenDelete_ThenRespondsSuccessMessageAndOkStatus() throws Exception {
        ThumbnailRequest payload = new ThumbnailRequest("1234567");
        String content = mapper.writeValueAsString(payload);

        when(thumbnailService.delete(any(), any()))
                .thenReturn(new SuccessResponse());

        mockMvc.perform(delete("/api/v1/thumbnail/1337")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }
}
