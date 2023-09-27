package com.github.karixdev.ratingyoutubethumbnailsapi.game.entity;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.constants.GameConstants;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.EmptyGameRoundsSetException;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.request.GameResultRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.github.karixdev.ratingyoutubethumbnailsapi.game.constants.GameConstants.GAME_TTL_MINUTES;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "Game")
@Table(name = "game")
public class Game {

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

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @Builder.Default
    private Set<GameRound> rounds = new LinkedHashSet<>();

    @Column(
            name = "last_activity",
            nullable = false
    )
    private LocalDateTime lastActivity;

    @Column(
            name = "has_ended",
            nullable = false
    )
    @Builder.Default
    private Boolean hasEnded = false;

    public boolean isGameExpired(LocalDateTime now) {
        return hasEnded || lastActivity.plusMinutes(GAME_TTL_MINUTES).isBefore(now);
    }

    public GameRound getLatestRound() {
        return rounds.stream()
                .min(Comparator.comparing(
                        GameRound::getCreatedAt,
                        LocalDateTime::compareTo
                ))
                .orElseThrow(EmptyGameRoundsSetException::new);
    }

    public void addRound(VideoDTO firstVideo, VideoDTO secondVideo, Clock clock) {
        GameRound newRound = GameRound.builder()
                .firstVideoId(firstVideo.id())
                .secondVideoId(secondVideo.id())
                .game(this)
                .createdAt(LocalDateTime.now(clock))
                .build();

        rounds.add(newRound);
    }

}
