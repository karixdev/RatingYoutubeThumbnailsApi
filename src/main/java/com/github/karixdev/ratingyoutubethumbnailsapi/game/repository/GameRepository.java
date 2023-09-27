package com.github.karixdev.ratingyoutubethumbnailsapi.game.repository;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {

    @Query("""
            SELECT game
            FROM Game game
            WHERE game.userId = :userId
            ORDER BY game.lastActivity DESC
            """)
    List<Game> findByUserIdOrderByLastActivityDesc(@Param("userId") UUID userId);

}
