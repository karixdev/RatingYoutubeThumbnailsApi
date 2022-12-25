package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GameRepositoryTest {
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
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
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
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .build());

        // When
        List<Game> result = underTest.findByUserOrderByLastActivityDesc(user);

        // Then
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getLastActivity())
                .isAfter(result.get(1).getLastActivity());
    }
}
