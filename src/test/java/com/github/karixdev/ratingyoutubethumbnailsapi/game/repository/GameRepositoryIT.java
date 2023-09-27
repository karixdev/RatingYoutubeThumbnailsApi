package com.github.karixdev.ratingyoutubethumbnailsapi.game.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.entity.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class GameRepositoryIT extends ContainersEnvironment {

    @Autowired
    GameRepository underTest;

    @Autowired
    TestEntityManager em;

    @Test
    void GivenUserId_WhenFindByUserIdOrderByLastActivityDesc_ThenReturnsUserGamesInCorrectOrder() {
        // Given
        UUID userId = UUID.randomUUID();

        LocalDateTime dateTime1 = LocalDateTime.of(2023, 1, 1, 1, 0);
        Game game1 = em.persist(TestUtils.createGame(userId, dateTime1));

        LocalDateTime dateTime2 = LocalDateTime.of(2023, 1, 1, 1, 5);
        Game game2 = em.persist(TestUtils.createGame(userId, dateTime2));

        LocalDateTime dateTime3 = LocalDateTime.of(2023, 1, 1, 1, 10);
        em.persist(TestUtils.createGame(UUID.randomUUID(), dateTime3));

        // When
        List<Game> result = underTest.findByUserIdOrderByLastActivityDesc(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(game2);
        assertThat(result.get(1)).isEqualTo(game1);

    }

}