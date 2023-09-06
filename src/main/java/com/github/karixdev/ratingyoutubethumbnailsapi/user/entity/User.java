package com.github.karixdev.ratingyoutubethumbnailsapi.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "User")
@Table(
        name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "app_user_email_unique",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "app_user_username_unique",
                        columnNames = "username"
                )
        }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    @EqualsAndHashCode.Include
    private String email;

    @Column(
            name = "username",
            nullable = false,
            unique = true
    )
    @EqualsAndHashCode.Include
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false
    )
    private UserRole role;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;
}
