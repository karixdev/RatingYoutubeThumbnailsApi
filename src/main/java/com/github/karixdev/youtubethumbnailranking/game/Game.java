package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "thumbnail1_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "game_thumbnail1_id_fk"
            )
    )
    @ToString.Exclude
    private Thumbnail thumbnail1;

    @ManyToOne
    @JoinColumn(
            name = "thumbnail2_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "game_thumbnail2_id_fk"
            )
    )
    @ToString.Exclude
    private Thumbnail thumbnail2;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) &&
                Objects.equals(thumbnail1.getId(), game.thumbnail1.getId()) &&
                Objects.equals(thumbnail2.getId(), game.thumbnail2.getId()) &&
                Objects.equals(user.getId(), game.user.getId()) &&
                Objects.equals(lastActivity, game.lastActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, thumbnail1.getId(), thumbnail2.getId(), user.getId(), lastActivity);
    }
}
