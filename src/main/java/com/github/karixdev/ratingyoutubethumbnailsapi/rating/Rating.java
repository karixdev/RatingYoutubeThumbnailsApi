package com.github.karixdev.ratingyoutubethumbnailsapi.rating;

import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rating_gen"
    )
    @SequenceGenerator(
            name = "rating_gen",
            sequenceName = "rating_seq",
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

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "rating_user_id_fk"
            )
    )
    private User user;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(
            name = "thumbnail_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "rating_thumbnail_id_fk"
            )
    )
    private Thumbnail thumbnail;

    @Column(
            name = "points",
            precision = 19,
            scale = 2,
            nullable = false
    )
    private BigDecimal points;
}
