package com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {
    @Query("""
            SELECT thumbnail
            FROM Thumbnail thumbnail
            WHERE thumbnail.youtubeVideoId = :youtubeVideoId
            """)
    Optional<Thumbnail> findByYoutubeVideoId(@Param("youtubeVideoId") String youtubeVideoId);

    @Query("""
            SELECT thumbnail
            FROM Thumbnail thumbnail
            LEFT JOIN FETCH thumbnail.ratings
            """)
    List<Thumbnail> findAllThumbnails();

    @Query(value = """
            SELECT t.*
            FROM thumbnail t
            LEFT JOIN (
                SELECT * FROM round r WHERE r.game_id = :gameId
            ) as r1 ON t.id = r1.thumbnail_2_id AND r1.thumbnail_1_id = :thumbnailId
            LEFT JOIN (
                SELECT * FROM round r WHERE r.game_id = :gameId
            ) as r2 ON t.id = r2.thumbnail_1_id AND r2.thumbnail_2_id = :thumbnailId
            INNER JOIN (
                SELECT
                    t.id AS thumbnail_id,
                    CASE
                        WHEN r.points IS NULL
                            THEN :defaultPoints
                        ELSE r.points
                        END AS points
                FROM rating r
                         RIGHT JOIN
                     thumbnail t ON r.thumbnail_id = t.id
                WHERE
                        r.user_id = :userId OR
                    r.user_id IS NULL
            ) AS r on r.thumbnail_id = t.id
            WHERE
                r1.id IS NULL AND
                r2.id IS NULL AND
                t.id != :thumbnailId
            ORDER BY ABS(r.points - :points)
            LIMIT 1;
            """, nativeQuery = true)
    Optional<Thumbnail> findThumbnailNotInGameWithClosestRating(
            @Param("gameId") Long gameId,
            @Param("userId") Long userId,
            @Param("defaultPoints") BigDecimal defaultPoints,
            @Param("thumbnailId") Long thumbnailId,
            @Param("points") BigDecimal points
    );
}
