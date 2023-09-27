package com.github.karixdev.ratingyoutubethumbnailsapi.video.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.entity.EntityState;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
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

    @Test
    void GivenListOfIds_WhenCountByIdNotIn_ThenReturnsCorrectSum() {
        // Given
        Video video1 = em.persist(TestUtils.createVideo("ytId1", UUID.randomUUID()));
        Video video2 = em.persist(TestUtils.createVideo("ytId2", UUID.randomUUID()));
        Video video3 = em.persist(TestUtils.createVideo("ytId3", UUID.randomUUID()));

        em.persist(TestUtils.createVideo("ytId4", UUID.randomUUID()));
        em.persist(TestUtils.createVideo("ytId5", UUID.randomUUID()));

        List<UUID> ids = List.of(video1.getId(), video2.getId(), video3.getId());

        // When
        Integer result = underTest.countByIdNotIn(ids);

        // Then
        assertThat(result).isEqualTo(2);
    }

    @Test
    void GivenListOfIds_WhenCountByIdIn_ThenReturnsCorrectSum() {
        // Given
        Video video1 = em.persist(TestUtils.createVideo("ytId1", UUID.randomUUID()));
        Video video2 = em.persist(TestUtils.createVideo("ytId2", UUID.randomUUID()));
        Video video3 = em.persist(TestUtils.createVideo("ytId3", UUID.randomUUID()));

        em.persist(TestUtils.createVideo("ytId4", UUID.randomUUID()));
        em.persist(TestUtils.createVideo("ytId5", UUID.randomUUID()));

        List<UUID> ids = List.of(video1.getId(), video2.getId(), video3.getId());

        // When
        Integer result = underTest.countByIdIn(ids);

        // Then
        assertThat(result).isEqualTo(3);
    }

    @Test
    void GivenListOfIdsAndPageable_WhenFindByIdIn_ThenReturnsCorrectPage() {
        // Given
        Video video1 = em.persist(TestUtils.createVideo("ytId1", UUID.randomUUID()));
        Video video2 = em.persist(TestUtils.createVideo("ytId2", UUID.randomUUID()));
        Video video3 = em.persist(TestUtils.createVideo("ytId3", UUID.randomUUID()));

        em.persist(TestUtils.createVideo("ytId4", UUID.randomUUID()));
        em.persist(TestUtils.createVideo("ytId5", UUID.randomUUID()));

        List<UUID> ids = List.of(video1.getId(), video2.getId(), video3.getId());
        PageRequest pageRequest = PageRequest.of(0, 2);

        // When
        Page<Video> result1 = underTest.findByIdIn(ids, pageRequest);
        Page<Video> result2 = underTest.findByIdIn(ids, pageRequest.next());

        // Then
        assertThat(result1.getTotalPages()).isEqualTo(2);
        assertThat(result1.getTotalElements()).isEqualTo(3);
        assertThat(result1.getNumberOfElements()).isEqualTo(2);
        assertThat(result1.getContent()).containsExactly(video1, video2);

        assertThat(result2.getNumberOfElements()).isEqualTo(1);
        assertThat(result2.getContent()).containsExactly(video3);
    }

    @Test
    void GivenListOfIdsAndPageable_WhenFindByIdNotIn_ThenReturnsCorrectPage() {
        // Given
        Video video1 = em.persist(TestUtils.createVideo("ytId1", UUID.randomUUID()));
        Video video2 = em.persist(TestUtils.createVideo("ytId2", UUID.randomUUID()));
        Video video3 = em.persist(TestUtils.createVideo("ytId3", UUID.randomUUID()));

        Video video4 = em.persist(TestUtils.createVideo("ytId4", UUID.randomUUID()));
        Video video5 = em.persist(TestUtils.createVideo("ytId5", UUID.randomUUID()));

        List<UUID> ids = List.of(video1.getId(), video2.getId(), video3.getId());
        PageRequest pageRequest = PageRequest.of(0, 1);

        // When
        Page<Video> result1 = underTest.findByIdNotIn(ids, pageRequest);
        Page<Video> result2 = underTest.findByIdNotIn(ids, pageRequest.next());

        // Then
        assertThat(result1.getTotalPages()).isEqualTo(2);
        assertThat(result1.getTotalElements()).isEqualTo(2);
        assertThat(result1.getNumberOfElements()).isEqualTo(1);
        assertThat(result1.getContent()).containsExactly(video4);

        assertThat(result2.getNumberOfElements()).isEqualTo(1);
        assertThat(result2.getContent()).containsExactly(video5);
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