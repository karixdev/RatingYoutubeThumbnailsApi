package com.github.karixdev.youtubethumbnailranking.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karixdev.youtubethumbnailranking.game.exception.GameHasNotEndedException;
import com.github.karixdev.youtubethumbnailranking.game.exception.InvalidWinnerIdException;
import com.github.karixdev.youtubethumbnailranking.game.payload.request.GameResultRequest;
import com.github.karixdev.youtubethumbnailranking.game.payload.response.GameResponse;
import com.github.karixdev.youtubethumbnailranking.shared.exception.PermissionDeniedException;
import com.github.karixdev.youtubethumbnailranking.shared.exception.ResourceNotFoundException;
import com.github.karixdev.youtubethumbnailranking.thumbnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = GameController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class GameControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    GameService gameService;

    ObjectMapper mapper = new ObjectMapper();

    Game game;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build();

        Thumbnail thumbnail1 = Thumbnail.builder()
                .id(1L)
                .addedBy(user)
                .url("thumbnail-url-1")
                .youtubeVideoId("youtube-id-1")
                .build();

        Thumbnail thumbnail2 = Thumbnail.builder()
                .id(2L)
                .addedBy(user)
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .build();

        game = Game.builder()
                .id(1L)
                .user(user)
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .lastActivity(LocalDateTime.now())
                .build();
    }

    @Test
    void GivenUserWhoHasJustStartedGame_WhenStart_ThenRespondsWithBadRequestStatus() throws Exception {
        doThrow(GameHasNotEndedException.class)
                .when(gameService)
                .start(any());

        mockMvc.perform(post("/api/v1/game/start")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GivenUser_WhenStart_ThenRespondsWithCorrectBodyAndOkStatus() throws Exception {
        when(gameService.start(any()))
                .thenReturn(new GameResponse(game));

        mockMvc.perform(post("/api/v1/game/start")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(1),
                        jsonPath("$.thumbnail1.id").value(1),
                        jsonPath("$.thumbnail1.url").value("thumbnail-url-1"),
                        jsonPath("$.thumbnail2.id").value(2),
                        jsonPath("$.thumbnail2.url").value("thumbnail-url-2")
                );
    }

    @Test
    void GivenNullWinnerId_WhenResult_ThenRespondsWithBadRequestStatus() throws Exception {
        mockMvc.perform(post("/api/v1/game/result/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GivenNotExistingGameId_WhenResult_ThenRespondsWithNotFoundStatus() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(gameService)
                .result(any(), any(), any());

        GameResultRequest payload = new GameResultRequest(1L);
        String content = mapper.writeValueAsString(payload);

        mockMvc.perform(post("/api/v1/game/result/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound());
    }

    @Test
    void GivenNotOwnerOfGame_WhenResult_ThenRespondsWithNotFoundStatus() throws Exception {
        doThrow(PermissionDeniedException.class)
                .when(gameService)
                .result(any(), any(), any());

        GameResultRequest payload = new GameResultRequest(1L);
        String content = mapper.writeValueAsString(payload);

        mockMvc.perform(post("/api/v1/game/result/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    void GivenInvalidWinnerId_WhenResult_ThenRespondsWithBadRequestStatus() throws Exception {
        doThrow(InvalidWinnerIdException.class)
                .when(gameService)
                .result(any(), any(), any());

        GameResultRequest payload = new GameResultRequest(1L);
        String content = mapper.writeValueAsString(payload);

        mockMvc.perform(post("/api/v1/game/result/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GivenValidWinnerIdAndValidUserAndValidGameId_WhenResult_ThenRespondsWithCorrectBodyAndOkStats() throws Exception {
        GameResultRequest payload = new GameResultRequest(1L);
        String content = mapper.writeValueAsString(payload);

        Thumbnail otherThumbnail = Thumbnail.builder()
                .id(3L)
                .addedBy(user)
                .url("thumbnail-url-3")
                .youtubeVideoId("youtube-id-3")
                .build();

        game.setThumbnail2(otherThumbnail);

        when(gameService.result(any(), any(), any()))
                .thenReturn(new GameResponse(game));


        mockMvc.perform(post("/api/v1/game/result/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(1),
                        jsonPath("$.thumbnail1.id").value(1),
                        jsonPath("$.thumbnail1.url").value("thumbnail-url-1"),
                        jsonPath("$.thumbnail2.id").value(3),
                        jsonPath("$.thumbnail2.url").value("thumbnail-url-3")
                );
    }
}
