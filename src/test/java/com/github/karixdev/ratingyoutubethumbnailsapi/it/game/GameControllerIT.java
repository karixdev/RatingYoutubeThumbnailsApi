package com.github.karixdev.ratingyoutubethumbnailsapi.it.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.GameRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.GameService;
import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.jwt.JwtService;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingProperties;
import com.github.karixdev.ratingyoutubethumbnailsapi.rating.RatingRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.Round;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.RoundRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.ThumbnailRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GameControllerIT extends ContainersEnvironment {
    @Autowired
    WebTestClient webClient;

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @Autowired
    GameService gameService;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    RatingProperties ratingProperties;

    @Autowired
    RoundRepository roundRepository;

    @Autowired
    Clock clock;

    @BeforeEach
    void tearDown() {
        roundRepository.deleteAll();
        gameRepository.deleteAll();
        ratingRepository.deleteAll();
        thumbnailRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldStartGameWithEmptyUserGameSet() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Thumbnail thumbnail1 = createThumbnail("url-1", "yt-id-1", user);
        Thumbnail thumbnail2 = createThumbnail("url-2", "yt-id-2", user);

        thumbnailRepository.saveAll(List.of(thumbnail1, thumbnail2));

        webClient.post().uri("/api/v1/game")
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk();

        Optional<Game> optionalGame = gameRepository.findAll().stream()
                .filter(game -> game.getUser().equals(user))
                .findFirst();
        assertThat(optionalGame).isNotEmpty();
        assertThat(optionalGame.get().getHasEnded()).isFalse();

        Optional<Round> optionalRound = roundRepository.findAll().stream()
                .filter(round -> round.getGame().equals(optionalGame.get()))
                .findFirst();
        assertThat(optionalRound).isNotEmpty();

        assertThat(optionalRound.get().getThumbnail1()).isIn(thumbnail1, thumbnail2);
        assertThat(optionalRound.get().getThumbnail2()).isIn(thumbnail1, thumbnail2);
    }

    @Test
    void shouldStartGameWhenUsersLatestGameHasEnded() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Thumbnail thumbnail1 = createThumbnail("url-1", "yt-id-1", user);
        Thumbnail thumbnail2 = createThumbnail("url-2", "yt-id-2", user);

        thumbnailRepository.saveAll(List.of(thumbnail1, thumbnail2));

        gameRepository.save(Game.builder()
                .user(user)
                .hasEnded(true)
                .lastActivity(LocalDateTime.now(clock))
                .build());

        webClient.post().uri("/api/v1/game")
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk();

        List<Game> games = gameRepository.findAll().stream()
                .filter(game -> game.getUser().equals(user))
                .sorted(Comparator.comparing(Game::getId))
                .toList();
        assertThat(games).hasSize(2);

        assertThat(games.get(1).getHasEnded()).isFalse();

        Optional<Round> optionalRound = roundRepository.findAll().stream()
                .filter(round -> round.getGame().equals(games.get(1)))
                .findFirst();
        assertThat(optionalRound).isNotEmpty();

        assertThat(optionalRound.get().getThumbnail1()).isIn(thumbnail1, thumbnail2);
        assertThat(optionalRound.get().getThumbnail2()).isIn(thumbnail1, thumbnail2);
    }

    @Test
    void shouldStartGameWhenUsersLatestGameIsExpired() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Thumbnail thumbnail1 = createThumbnail("url-1", "yt-id-1", user);
        Thumbnail thumbnail2 = createThumbnail("url-2", "yt-id-2", user);

        thumbnailRepository.saveAll(List.of(thumbnail1, thumbnail2));

        gameRepository.save(Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now(clock).minusHours(10))
                .build());

        webClient.post().uri("/api/v1/game")
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk();

        List<Game> games = gameRepository.findAll().stream()
                .filter(game -> game.getUser().equals(user))
                .sorted(Comparator.comparing(Game::getId))
                .toList();
        assertThat(games).hasSize(2);

        assertThat(games.get(1).getHasEnded()).isFalse();

        Optional<Round> optionalRound = roundRepository.findAll().stream()
                .filter(round -> round.getGame().equals(games.get(1)))
                .findFirst();
        assertThat(optionalRound).isNotEmpty();

        assertThat(optionalRound.get().getThumbnail1()).isIn(thumbnail1, thumbnail2);
        assertThat(optionalRound.get().getThumbnail2()).isIn(thumbnail1, thumbnail2);
    }

    @Test
    void shouldRetrieveUserActiveGame() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Thumbnail thumbnail1 = createThumbnail("url-1", "yt-id-1", user);
        Thumbnail thumbnail2 = createThumbnail("url-2", "yt-id-2", user);

        thumbnailRepository.saveAll(List.of(thumbnail1, thumbnail2));

        Game game = gameRepository.save(Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now(clock).minusSeconds(45))
                .build());

        roundRepository.save(Round.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .game(game)
                .createdAt(LocalDateTime.now(clock))
                .build());

        webClient.post().uri("/api/v1/game")
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(game.getId())
                .jsonPath("$.thumbnails.[0].id").isEqualTo(thumbnail1.getId())
                .jsonPath("$.thumbnails.[1].id").isEqualTo(thumbnail2.getId());
    }

    @Test
    void shouldContinueGameWithOpponentWhichHasClosestRatingToWinner() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        thumbnail1.addRating(createRating(user, thumbnail1, 1450));

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);
        thumbnail2.addRating(createRating(user, thumbnail2, 1500));

        Thumbnail thumbnail3 = createThumbnail("thumbnail-url3", "yt-id3", user);
        thumbnail3.addRating(createRating(user, thumbnail3, 1800));

        Thumbnail thumbnail4 = createThumbnail("thumbnail-url4", "yt-id4", user);
        thumbnail4.addRating(createRating(user, thumbnail4, 1450));

        thumbnailRepository.saveAll(List.of(thumbnail1, thumbnail2, thumbnail3, thumbnail4));

        Game game = gameRepository.save(Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now(clock).minusSeconds(45))
                .build());

        roundRepository.save(Round.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail4)
                .game(game)
                .createdAt(LocalDateTime.now(clock))
                .build());

        String payload = """
                {
                    "winner_id": %d
                }
                """.formatted(thumbnail1.getId());

        webClient.post().uri("/api/v1/game")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(game.getId())
                .jsonPath("$.thumbnails.[0].id").isEqualTo(thumbnail1.getId())
                .jsonPath("$.thumbnails.[1].id").isEqualTo(thumbnail2.getId());
    }

    @Test
    void shouldContinueGameWithOpponentWhichHasClosestRatingToWinnerAndWasNotAlreadyInGame() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        thumbnail1.addRating(createRating(user, thumbnail1, 1450));

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);
        thumbnail2.addRating(createRating(user, thumbnail2, 1500));

        Thumbnail thumbnail3 = createThumbnail("thumbnail-url3", "yt-id3", user);
        thumbnail3.addRating(createRating(user, thumbnail3, 1800));

        thumbnailRepository.saveAll(List.of(thumbnail1, thumbnail2, thumbnail3));

        Game game = gameRepository.save(Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now(clock).minusSeconds(45))
                .build());

        roundRepository.save(Round.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .game(game)
                .createdAt(LocalDateTime.now(clock))
                .build());

        String payload = """
                {
                    "winner_id": %d
                }
                """.formatted(thumbnail1.getId());

        webClient.post().uri("/api/v1/game")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(game.getId())
                .jsonPath("$.thumbnails.[0].id").isEqualTo(thumbnail1.getId())
                .jsonPath("$.thumbnails.[1].id").isEqualTo(thumbnail3.getId());
    }

    @Test
    void shouldContinueGameWhereThereIsOnlyOneOpponentAvailable() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        thumbnail1.addRating(createRating(user, thumbnail1, 1450));

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);
        thumbnail2.addRating(createRating(user, thumbnail2, 1500));

        Thumbnail thumbnail3 = createThumbnail("thumbnail-url3", "yt-id3", user);
        thumbnail3.addRating(createRating(user, thumbnail3, 1800));

        thumbnailRepository.saveAll(List.of(thumbnail1, thumbnail2, thumbnail3));

        Game game = gameRepository.save(Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now(clock).minusSeconds(45))
                .build());

        roundRepository.save(Round.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .game(game)
                .createdAt(LocalDateTime.now(clock).minusSeconds(30))
                .build());

        roundRepository.save(Round.builder()
                .thumbnail1(thumbnail2)
                .thumbnail2(thumbnail3)
                .game(game)
                .createdAt(LocalDateTime.now(clock).minusSeconds(15))
                .build());

        String payload = """
                {
                    "winner_id": %d
                }
                """.formatted(thumbnail3.getId());

        webClient.post().uri("/api/v1/game")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(game.getId())
                .jsonPath("$.thumbnails.[0].id").isEqualTo(thumbnail3.getId())
                .jsonPath("$.thumbnails.[1].id").isEqualTo(thumbnail1.getId());
    }

    @Test
    void shouldNotContinueGameWhenThereIsNotAvailableOpponent() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Thumbnail thumbnail1 = createThumbnail("thumbnail-url1", "yt-id1", user);
        thumbnail1.addRating(createRating(user, thumbnail1, 1450));

        Thumbnail thumbnail2 = createThumbnail("thumbnail-url2", "yt-id2", user);
        thumbnail2.addRating(createRating(user, thumbnail2, 1500));

        thumbnailRepository.saveAll(List.of(thumbnail1, thumbnail2));

        Game game = gameRepository.save(Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now(clock).minusSeconds(45))
                .build());

        roundRepository.save(Round.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .game(game)
                .createdAt(LocalDateTime.now(clock).minusSeconds(30))
                .build());

        String payload = """
                {
                    "winner_id": %d
                }
                """.formatted(thumbnail1.getId());

        webClient.post().uri("/api/v1/game")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        Optional<Game> optionalGame = gameRepository.findById(game.getId());
        assertThat(optionalGame).isNotEmpty();
        assertThat(optionalGame.get().getHasEnded()).isTrue();
    }

    @Test
    void shouldRespondWith404WhenTryingToEndNotExistingGame() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        webClient.post().uri("/api/v1/game/end/121")
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldRespondWith403WhenUserTriesToEndNotHisGame() {
        User user = userRepository.save(createUser());
        String jwt = jwtService.createToken(new UserPrincipal(user));

        Game game = Game.builder()
                .lastActivity(LocalDateTime.now(clock))
                .hasEnded(false)
                .user(userRepository.save(craeteOtherUser()))
                .build();

        gameRepository.save(game);

        webClient.post().uri("/api/v1/game/end/" + game.getId())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void shouldRespondWith400WhenTryingToEndAlreadyEndedGame() {
        User user = userRepository.save(createUser());
        Game game = Game.builder()
                .lastActivity(LocalDateTime.now(clock))
                .user(user)
                .hasEnded(true)
                .build();

        gameRepository.save(game);

        String jwt = jwtService.createToken(new UserPrincipal(user));

        webClient.post().uri("/api/v1/game/end/" + game.getId())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldEndGame() {
        User user = userRepository.save(createUser());
        Game game = Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now(clock))
                .build();

        gameRepository.save(game);

        String jwt = jwtService.createToken(new UserPrincipal(user));

        webClient.post().uri("/api/v1/game/end/" + game.getId())
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody();

        game = gameRepository.findById(game.getId()).orElseThrow();

        assertThat(game.getHasEnded()).isTrue();
    }

    private static Rating createRating(User user, Thumbnail thumbnail, double points) {
        return Rating.builder()
                .user(user)
                .thumbnail(thumbnail)
                .points(BigDecimal.valueOf(points))
                .build();
    }

    private static Thumbnail createThumbnail(String url, String ytId, User user) {
        return Thumbnail.builder()
                .url(url)
                .youtubeVideoId(ytId)
                .addedBy(user)
                .build();
    }

    private static User createUser() {
        return User.builder()
                .email("email@email.com")
                .username("username")
                .password("password")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(true)
                .build();
    }

    private static User craeteOtherUser() {
        return User.builder()
                .email("email2@email.com")
                .username("username2")
                .password("password2")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(true)
                .build();
    }
}
