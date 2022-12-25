package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("""
            SELECT game
            FROM Game game
            WHERE game.user = :user
            ORDER BY game.lastActivity DESC
            """)
    List<Game> findByUserOrderByLastActivityDesc(@Param("user") User user);
}
