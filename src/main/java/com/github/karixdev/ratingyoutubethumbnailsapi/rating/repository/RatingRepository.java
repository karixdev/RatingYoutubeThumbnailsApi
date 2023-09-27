package com.github.karixdev.ratingyoutubethumbnailsapi.rating.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {
    @Query("""
            SELECT rating
            FROM Rating rating
            WHERE rating.userId = :userId AND
                  rating.videoId = :videoId
            """)
    Optional<Rating> findByUserIdAndVideoId(@Param("userId") UUID userId, @Param("videoId") UUID videoId);

    @Query("""
            SELECT rating
            FROM Rating rating
            WHERE rating.userId = :userId
            """)
    List<Rating> findByUserId(@Param("userId") UUID userId);
}
