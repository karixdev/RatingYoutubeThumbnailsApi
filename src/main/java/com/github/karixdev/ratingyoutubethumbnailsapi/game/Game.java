package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.exception.GameHasAlreadyEndedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.Round;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.exception.EmptyRoundSetException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.PermissionDeniedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import lombok.*;

import jakarta.persistence.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "game")
public class Game {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "game_gen"
    )
    @SequenceGenerator(
            name = "game_gen",
            sequenceName = "game_seq",
            allocationSize = 1
    )
    @Column(
            name = "id",
            nullable = false,
            updatable = false
    )
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "game_user_id_fk"
            )
    )
    @ToString.Exclude
    private User user;

    @Column(
            name = "last_activity",
            nullable = false
    )
    private LocalDateTime lastActivity;

    @Column(name = "has_ended")
    @Builder.Default
    private Boolean hasEnded = false;

    @OneToMany(
            mappedBy = "game",
            orphanRemoval = true,
            cascade = CascadeType.ALL
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Round> rounds = new LinkedHashSet<>();

    public void addRound(Thumbnail thumbnail1, Thumbnail thumbnail2, Clock clock) {
        rounds.add(Round.builder()
                .thumbnail1(thumbnail1)
                .thumbnail2(thumbnail2)
                .game(this)
                .createdAt(LocalDateTime.now(clock))
                .build());
    }

    public Round getLatestRound() {
        return rounds.stream()
                .max(Comparator.comparing(
                        Round::getCreatedAt,
                        LocalDateTime::compareTo
                ))
                .orElseThrow(EmptyRoundSetException::new);
    }

    public boolean isGameExpired(Clock clock, int maxTimeOfNoGameUpdate) {
        return hasEnded || LocalDateTime.now(clock).isAfter(lastActivity.plusMinutes(maxTimeOfNoGameUpdate));
    }

    private boolean isOwnedBy(User user) {
        return this.user.equals(user);
    }

    public void endGame(User user, Clock clock, int maxTimeOfNoGameUpdate) {
        if (!isOwnedBy(user) && !user.isAdmin()) {
            throw new PermissionDeniedException("You are not the owner of the game");
        }

        if (isGameExpired(clock, maxTimeOfNoGameUpdate)) {
            throw new GameHasAlreadyEndedException();
        }

        setHasEnded(true);
    }
}
