package com.github.karixdev.ratingyoutubethumbnailsapi.round;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "round")
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private UUID id;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "thumbnail_1_id", nullable = false)
    private Thumbnail thumbnail1;

    @ToString.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "thumbnail_2_id", nullable = false)
    private Thumbnail thumbnail2;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;
}
