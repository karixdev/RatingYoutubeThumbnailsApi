package com.github.karixdev.ratingyoutubethumbnailsapi.video.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("""
            SELECT COUNT(video)
            FROM Video video
            WHERE video.id NOT IN :ids
            """)
    Integer countByIdNotIn(@Param("ids") List<UUID> ids);

    @Query("""
            SELECT COUNT(video)
            FROM Video video
            WHERE video.id IN :ids
            """)
    Integer countByIdIn(@Param("ids") List<UUID> ids);

    @Query("""
            SELECT video
            FROM Video video
            WHERE video.id IN :ids
            """)
    Page<Video> findByIdIn(@Param("ids") List<UUID> ids, Pageable pageable);

    @Query("""
            SELECT video
            FROM Video video
            WHERE video.id NOT IN :ids
            """)
    Page<Video> findByIdNotIn(@Param("ids") List<UUID> ids, Pageable pageable);
}
