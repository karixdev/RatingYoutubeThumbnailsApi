package com.github.karixdev.ratingyoutubethumbnailsapi.video.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.entity.EntityState;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class VideoRepositoryIT extends ContainersEnvironment {

    @Autowired
    TestEntityManager em;

    @Autowired
    VideoRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void GivenVideoYoutubeIdThatDoesNotExist_WhenFindByYoutubeId_ThenReturnsEmptyOptional() {
        // Given
        String youtubeId = "youtube-id-2";
        em.persist(createVideo("youtube-id", EntityState.PERSISTED));

        // When
        Optional<Video> result = underTest.findByYoutubeIdAndNotRemovedState(youtubeId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenVideoYoutubeIdThatIsInRemovedState_WhenFindByYoutubeId_ThenReturnsEmptyOptional() {
        // Given
        String youtubeId = "youtube-id";
        em.persist(createVideo(youtubeId, EntityState.REMOVED));
        em.persist(createVideo("youtube-id-2", EntityState.PERSISTED));

        // When
        Optional<Video> result = underTest.findByYoutubeIdAndNotRemovedState(youtubeId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void GivenYoutubeIdThatIsInPersistedState_WhenFindByYoutubeId_ThenReturnsOptionalWithProperEntity() {
        // Given
        String youtubeId = "youtube-id";
        Video video = em.persist(createVideo(youtubeId, EntityState.PERSISTED));
        em.persist(createVideo("youtube-id-2", EntityState.PERSISTED));

        // When
        Optional<Video> result = underTest.findByYoutubeIdAndNotRemovedState(youtubeId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(video);
    }

    private static Video createVideo(String youtubeId, EntityState state) {
        return Video.builder()
                .userId(UUID.randomUUID())
                .youtubeId(youtubeId)
                .defaultResThumbnail("default")
                .mediumResThumbnail("medium")
                .highResThumbnail("high")
                .standardResThumbnail("standard")
                .maxResThumbnail("max")
                .state(state)
                .createdAt(LocalDateTime.now())
                .build();
    }

}