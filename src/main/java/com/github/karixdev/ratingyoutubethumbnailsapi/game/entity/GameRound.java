package com.github.karixdev.ratingyoutubethumbnailsapi.game.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "GameRound")
@Table(name = "game_round")
public class GameRound {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "id",
            nullable = false,
            unique = true
    )
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "game_id",
            nullable = false
    )
    private Game game;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "first_video_id",
            nullable = false
    )
    private UUID firstVideoId;

    @Column(
            name = "second_video_id",
            nullable = false
    )
    private UUID secondVideoId;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

}
