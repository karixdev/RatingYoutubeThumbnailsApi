package com.github.karixdev.ratingyoutubethumbnailsapi.it.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.GameRepository;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GameRepositoryTest extends ContainersEnvironment {
    @Autowired
    GameRepository underTest;

    @Autowired
    TestEntityManager em;

    User user;

    Thumbnail thumbnail1;

    Thumbnail thumbnail2;

    Game game;

    @BeforeEach
    void setUp() {
        user = em.persist(User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build());

        thumbnail1 = em.persist(Thumbnail.builder()
                .addedBy(user)
                .url("thumbnail-url-1")
                .youtubeVideoId("youtube-id-1")
                .build());

        thumbnail2 = em.persist(Thumbnail.builder()
                .addedBy(user)
                .url("thumbnail-url-2")
                .youtubeVideoId("youtube-id-2")
                .build());

        game = em.persistAndFlush(Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now())
                .build());
    }

    @Test
    void GivenUserThatHasNoGames_WhenFindByUserOrderByLastActivityDesc_ThenEmptyList() {
        // Given
        User otherUser = em.persistAndFlush(User.builder()
                .email("email-2@email.com")
                .password("password")
                .username("username-2")
                .userRole(UserRole.ROLE_USER)
                .isEnabled(Boolean.TRUE)
                .build());

        // When
        List<Game> result = underTest.findByUserOrderByLastActivityDesc(otherUser);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenUser_WhenFindByUserOrderByLastActivityDesc_ThenReturnsProperlySortedList() {
        em.persistAndFlush(Game.builder()
                .user(user)
                .lastActivity(LocalDateTime.now().plusMinutes(20))
                .build());

        // When
        List<Game> result = underTest.findByUserOrderByLastActivityDesc(user);

        // Then
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getLastActivity())
                .isAfter(result.get(1).getLastActivity());
    }

    @Test
    void GivenUserAndHasEnded_WhenFindByUserAndHasEndedOrderByLastActivityDesc_ThenReturnsCorrectList() {
        // Given
        Boolean hasEnded = Boolean.FALSE;

        for (int i = 3; i <= 4; i++) {
            Thumbnail otherThumbnail1 = em.persist(Thumbnail.builder()
                    .addedBy(user)
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());

            Thumbnail otherThumbnail2 = em.persist(Thumbnail.builder()
                    .addedBy(user)
                    .url("thumbnail-url-" + i)
                    .youtubeVideoId("youtube-id-" + i)
                    .build());

            Game otherGame = Game.builder()
                    .user(user)
                    .lastActivity(LocalDateTime.now().plusMinutes(i))
                    .build();

            if (i == 4) {
                otherGame.setHasEnded(Boolean.TRUE);
            }

            em.persistAndFlush(otherGame);
        }

        // When
        List<Game> result =
                underTest.findByUserAndHasEndedOrderByLastActivityDesc(
                        user, hasEnded);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLastActivity())
                .isAfter(result.get(1).getLastActivity());
    }
}
