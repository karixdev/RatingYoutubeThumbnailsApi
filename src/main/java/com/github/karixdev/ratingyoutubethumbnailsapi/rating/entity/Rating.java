package com.github.karixdev.ratingyoutubethumbnailsapi.rating.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "Rating")
@Table(
        name = "rating",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "rating_user_id_and_video_id_unique",
                        columnNames = {"user_id", "video_id"}
                )
        }
)
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "id",
            nullable = false,
            unique = true
    )
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "video_id",
            nullable = false
    )
    private UUID videoId;

    @Column(
            name = "points",
            nullable = false
    )
    private BigDecimal points;
}
