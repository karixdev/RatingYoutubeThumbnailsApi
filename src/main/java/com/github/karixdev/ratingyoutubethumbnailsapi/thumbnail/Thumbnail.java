package com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import lombok.*;

import jakarta.persistence.*;
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
@Table(name = "thumbnail")
public class Thumbnail {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "thumbnail_gen"
    )
    @SequenceGenerator(
            name = "thumbnail_gen",
            sequenceName = "thumbnail_seq",
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

    @Column(
            name = "youtube_video_id",
            nullable = false
    )
    private String youtubeVideoId;

    @Column(
            name = "url",
            nullable = false
    )
    private String url;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(
            name = "added_by_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "added_by_id_fk"
            )
    )
    private User addedBy;

    @OneToMany(
            mappedBy = "thumbnail",
            orphanRemoval = true,
            cascade = CascadeType.ALL
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Rating> ratings = new LinkedHashSet<>();
}
