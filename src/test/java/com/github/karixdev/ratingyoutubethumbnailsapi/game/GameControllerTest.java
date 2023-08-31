package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasAlreadyEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasNotEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.InvalidWinnerIdException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request.GameResultRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.response.GameResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.PermissionDeniedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void GivenNotExistingGameId_WhenEnd_ThenRespondsWithNotFoundStatus() throws Exception {
        doThrow(ResourceNotFoundException.class)
                .when(gameService)
                .end(any(), any());

        mockMvc.perform(post("/api/v1/game/end/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void GivenNotOwnerOfGame_WhenEnd_ThenRespondsWithForbiddenStatus() throws Exception {
        doThrow(PermissionDeniedException.class)
                .when(gameService)
                .end(any(), any());

        mockMvc.perform(post("/api/v1/game/end/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void GivenGameIdWhichHasEnded_WhenEnd_ThenRespondsWithBadRequestStatus() throws Exception {
        doThrow(GameHasAlreadyEndedException.class)
                .when(gameService)
                .end(any(), any());

        mockMvc.perform(post("/api/v1/game/end/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void GivenValidGameIdAndValidUser_WhenEnd_ThenRespondsWithCorrectBodyAndOkStats() throws Exception {
        when(gameService.end(any(), any()))
                .thenReturn(new SuccessResponse());

        mockMvc.perform(post("/api/v1/game/end/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));
    }

//    @Test
//    void GivenUserWhoHasZeroNotEndedGames_WhenGetUserActualActiveGame_ThenRespondsWithNotFoundStatus() throws Exception {
//        doThrow(ResourceNotFoundException.class)
//                .when(gameService)
//                .getUserActualActiveGame(any());
//
//        mockMvc.perform(get("/api/v1/game")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void GivenUserWhoHasNotEndedGamesButTheyAreExpired_WhenGetUserActualActiveGame_ThenRespondsWithNotFoundStatus() throws Exception {
//        doThrow(ResourceNotFoundException.class)
//                .when(gameService)
//                .getUserActualActiveGame(any());
//
//        mockMvc.perform(get("/api/v1/game")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void GivenUserWhoHasNotEndedGamesAndTheyAreNotExpired_WhenGetUserActualActiveGame_ThenRespondsCorrectBodyAndOkStats() throws Exception {
//        when(gameService.getUserActualActiveGame(any()))
//                .thenReturn(new GameResponse(game));
//
//        mockMvc.perform(get("/api/v1/game")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpectAll(
//                        jsonPath("$.id").value(1),
//                        jsonPath("$.thumbnail1.id").value(1),
//                        jsonPath("$.thumbnail1.url").value("thumbnail-url-1"),
//                        jsonPath("$.thumbnail2.id").value(2),
//                        jsonPath("$.thumbnail2.url").value("thumbnail-url-2")
//                );
//    }
}
