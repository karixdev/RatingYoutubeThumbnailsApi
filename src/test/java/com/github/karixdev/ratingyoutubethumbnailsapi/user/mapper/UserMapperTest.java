package com.github.karixdev.ratingyoutubethumbnailsapi.user.mapper;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    UserMapper underTest = Mappers.getMapper(UserMapper.class);

    @Test
    void GivenUser_WhenUserToDTO_ThenReturnsCorrectDTO() {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("email@email.com")
                .username("username")
                .password("password")
                .role(UserRole.USER)
                .build();

        // When
        UserDTO result = underTest.userToDTO(user);

        // Then
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.username()).isEqualTo(user.getUsername());
        assertThat(result.password()).isEqualTo(user.getPassword());
        assertThat(result.role()).isEqualTo(user.getRole());
    }
}