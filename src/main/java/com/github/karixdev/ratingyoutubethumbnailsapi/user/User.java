package com.github.karixdev.ratingyoutubethumbnailsapi.user;

import lombok.*;

import jakarta.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "app_user_username_unique",
                        columnNames = "username"
                ),
                @UniqueConstraint(
                        name = "app_user_email_unique",
                        columnNames = "email"
                )
        }
)
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "app_user_gen"
    )
    @SequenceGenerator(
            name = "app_user_gen",
            sequenceName = "app_user_seq",
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
            name = "username",
            nullable = false
    )
    private String username;

    @Column(
            name = "email",
            nullable = false
    )
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "user_role",
            nullable = false
    )
    private UserRole userRole;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @Column(
            name = "is_enabled",
            nullable = false
    )
    private Boolean isEnabled;

    public boolean isAdmin() {
        return userRole == UserRole.ROLE_ADMIN;
    }
}
