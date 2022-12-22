package com.github.karixdev.youtubethumbnailranking.rating;

import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.user.User;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return Objects.equals(id, rating.id) &&
                Objects.equals(user.getId(), rating.user.getId())
                && Objects.equals(thumbnail.getId(), rating.thumbnail.getId())
                && Objects.equals(points, rating.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user.getId(), thumbnail.getId(), points);
    }
}
