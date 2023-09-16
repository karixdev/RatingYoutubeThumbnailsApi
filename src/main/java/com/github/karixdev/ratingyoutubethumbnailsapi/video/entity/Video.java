package com.github.karixdev.ratingyoutubethumbnailsapi.video.entity;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.entity.EntityState;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ForbiddenActionException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "Video")
@Table(
        name = "video",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "video_youtube_id_and_sate_and_created_at_unique",
                        columnNames = {
                                "youtube_id",
                                "state",
                                "created_at"
                        }
                )
        }
)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "id",
            unique = true,
            updatable = false
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
            name = "youtube_id",
            nullable = false
    )
    @EqualsAndHashCode.Include
    private String youtubeId;

    @Column(
            name = "default_res_thumbnail",
            nullable = false
    )
    private String defaultResThumbnail;

    @Column(
            name = "medium_res_thumbnail",
            nullable = false
    )
    private String mediumResThumbnail;

    @Column(
            name = "high_res_thumbnail",
            nullable = false
    )
    private String highResThumbnail;

    @Column(
            name = "standard_res_thumbnail",
            nullable = false
    )
    private String standardResThumbnail;

    @Column(
            name = "max_res_thumbnail",
            nullable = false
    )
    private String maxResThumbnail;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "state",
            nullable = false
    )
    @Builder.Default
    private EntityState state = EntityState.PERSISTED;

    public void moveIntoRemovedState(UserDTO user) {
        if (!userId.equals(user.id()) && user.role() != UserRole.ADMIN) {
            throw new ForbiddenActionException();
        }

        setState(EntityState.REMOVED);
    }

}
