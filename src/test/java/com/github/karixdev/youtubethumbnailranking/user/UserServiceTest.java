package com.github.karixdev.youtubethumbnailranking.user;

import com.github.karixdev.youtubethumbnailranking.user.exception.EmailNotAvailableException;
import com.github.karixdev.youtubethumbnailranking.user.exception.UsernameNotAvailableException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserService underTest;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .password("password")
                .username("username")
                .userRole(UserRole.ROLE_USER)
                .build();
    }

    @Test
    void GivenNotAvailableEmail_WhenIsEmailAvailable_ThenReturnsFalse() {
        // Given
        String email = "email@email.com";

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        // When
        boolean result = underTest.isEmailAvailable(email);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void GivenAvailableEmail_WhenIsEmailAvailable_ThenReturnsTrue() {
        // Given
        String email = "available@email.com";

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        // When
        boolean result = underTest.isEmailAvailable(email);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void GivenNotAvailableUsername_WhenIsUsernameAvailable_ThenReturnsFalse() {
        // Given
        String username = "username";

        when(userRepository.findByUsername(any()))
                .thenReturn(Optional.of(user));

        // When
        boolean result = underTest.isUsernameAvailable(username);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void GivenAvailableUsername_WhenIsUsernameAvailable_ThenReturnsTrue() {
        // Given
        String username = "available";

        when(userRepository.findByUsername(any()))
                .thenReturn(Optional.empty());

        // When
        boolean result = underTest.isUsernameAvailable(username);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void GivenNotAvailableEmail_WhenCreateUser_ThenThrowsNotAvailableEmailExceptionWithCorrectMessage() {
        // Given
        String email = "email@email.com";

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() ->
                underTest.createUser(
                        user.getEmail(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getUserRole()))
                .isInstanceOf(EmailNotAvailableException.class)
                .hasMessage("Email not available");
    }

    @Test
    void GivenNotAvailableUsername_WhenCreateUser_ThenThrowsNotAvailableUsernameExceptionWithCorrectMessage() {
        // Given
        String username = "username";

        when(userRepository.findByUsername(any()))
                .thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() ->
                underTest.createUser(
                        user.getEmail(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getUserRole()))
                .isInstanceOf(UsernameNotAvailableException.class)
                .hasMessage("Username not available");
    }

    @Test
    void GivenValidCredentials_WhenCreateUser_ThenReturnsCorrectUser() {
        // Given
        String email = user.getEmail();
        String username = user.getUsername();
        String password = user.getPassword();
        UserRole userRole = user.getUserRole();

        when(passwordEncoder.encode(any()))
                .thenReturn(user.getPassword());

        when(userRepository.save(any()))
                .thenReturn(user);

        // When
        User result = underTest.createUser(email, username, password, userRole);

        // Then
        assertThat(result).isEqualTo(result);
    }

}
