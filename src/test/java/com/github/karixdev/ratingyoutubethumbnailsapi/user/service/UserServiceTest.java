package com.github.karixdev.ratingyoutubethumbnailsapi.user.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.NewUserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.exception.UnavailableUserEmailException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.exception.UnavailableUserUsernameException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.mapper.UserMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    UserService underTest;

    @Mock
    UserRepository repository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserMapper mapper;

    @Test
    void GivenNewUserDTOWithUnavailableEmail_WhenCreate_ThenThrowsUnavailableUserEmailException() {
        // Given
        NewUserDTO dto = new NewUserDTO(
                "email@email.com",
                "username",
                "password",
                UserRole.USER
        );

        when(repository.findByEmail(eq(dto.email())))
                .thenReturn(Optional.of(new User()));

        // When & Then
        assertThatThrownBy(() -> underTest.create(dto))
                .isInstanceOf(UnavailableUserEmailException.class);
    }

    @Test
    void GivenNewUserDTOWithUnavailableUsername_WhenCreate_ThenThrowsUnavailableUserUsernameException() {
        // Given
        NewUserDTO dto = new NewUserDTO(
                "email@email.com",
                "username",
                "password",
                UserRole.USER
        );

        when(repository.findByEmail(eq(dto.email())))
                .thenReturn(Optional.empty());

        when(repository.findByUsername(eq(dto.username())))
                .thenReturn(Optional.of(new User()));

        // When & Then
        assertThatThrownBy(() -> underTest.create(dto))
                .isInstanceOf(UnavailableUserUsernameException.class);
    }

    @Test
    void GivenNewUserDTOWithValidData_WhenCreate_ThenSavesUserAndMapsItToDTO() {
        // Given
        NewUserDTO dto = new NewUserDTO(
                "email@email.com",
                "username",
                "password",
                UserRole.USER
        );

        when(repository.findByEmail(eq(dto.email())))
                .thenReturn(Optional.empty());

        when(repository.findByUsername(eq(dto.username())))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(eq(dto.password())))
                .thenReturn("hash");

        User user = User.builder()
                .email("email@email.com")
                .username("username")
                .password("hash")
                .role(UserRole.USER)
                .build();

        // When
        underTest.create(dto);

        // Then
        verify(repository).save(eq(user));
        verify(mapper).userToDTO(eq(user));
    }

    @Test
    void GivenEmailThatNoUserHas_WhenLoadUserByUsername_ThenThrowUsernameNotFoundException() {
        // Given
        String email = "email@email.com";

        when(repository.findByEmail(eq(email)))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void GivenEmail_WhenLoadUserByUsername_ThenUserIsMappedIntoUserDTO() {
        // Given
        String email = "email@email.com";

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .username("username")
                .password("password")
                .role(UserRole.USER)
                .build();

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getPassword()
        );

        when(repository.findByEmail(eq(email)))
                .thenReturn(Optional.of(user));

        when(mapper.userToDTO(eq(user)))
                .thenReturn(userDTO);

        // When
        underTest.loadUserByUsername(email);

        // Then
        verify(mapper).userToDTO(eq(user));
    }
}