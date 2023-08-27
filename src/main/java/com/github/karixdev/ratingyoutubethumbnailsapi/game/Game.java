package com.github.karixdev.ratingyoutubethumbnailsapi.game;

import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
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

    @Column(name = "has_ended")
    @Builder.Default
    private Boolean hasEnded = Boolean.FALSE;
}
