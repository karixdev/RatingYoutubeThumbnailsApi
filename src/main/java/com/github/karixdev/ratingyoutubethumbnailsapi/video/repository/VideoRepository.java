package com.github.karixdev.ratingyoutubethumbnailsapi.video.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    @Query("""
            SELECT video
            FROM Video video
            WHERE video.youtubeId = :youtubeId AND
                  video.state != 'REMOVED'
            """)
    Optional<Video> findByYoutubeIdAndNotRemovedState(@Param("youtubeId") String youtubeId);
}
