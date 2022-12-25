package com.github.karixdev.youtubethumbnailranking.rating;

import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("""
            SELECT rating
            FROM Rating rating
            WHERE rating.thumbnail = :thumbnail
            AND rating.user = :user
            """)
    Optional<Rating> findByThumbnailAndUser(
            @Param("thumbnail") Thumbnail thumbnail,
            @Param("user") User user
    );

    @Query("""
            SELECT rating
            FROM Rating rating
            WHERE rating.user = :user
            AND rating.thumbnail != :thumbnail
            """)
    List<Rating> findByUserAndThumbnailNot(
            @Param("thumbnail") Thumbnail thumbnail,
            @Param("user") User user
    );

}
