package com.github.karixdev.ratingyoutubethumbnailsapi.user;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.exception.EmailNotAvailableException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.exception.UsernameNotAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
                .isEnabled(Boolean.FALSE)
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
                        user.getUserRole(),
                        user.getIsEnabled()))
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
                        user.getUserRole(),
                        user.getIsEnabled()))
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
        Boolean isEnabled = user.getIsEnabled();

        when(passwordEncoder.encode(any()))
                .thenReturn(user.getPassword());

        when(userRepository.save(any()))
                .thenReturn(user);

        // When
        User result = underTest.createUser(email, username, password, userRole, isEnabled);

        // Then
        assertThat(result).isEqualTo(result);
        verify(userRepository).save(any());
    }

    @Test
    void GivenUser_WhenEnableUser_ThenSetsIsEnabledToTrue() {
        // Given
        user.setIsEnabled(Boolean.FALSE);

        // When
        underTest.enableUser(user);

        // Then
        assertThat(user.getIsEnabled()).isTrue();
        verify(userRepository).save(any());
    }

    @Test
    void GivenNotExistingUserEmail_WhenFindByEmail_ThenThrowsResourceNotFoundExceptionWithProperMessage() {
        // Given
        String email = "i-do-not-exist@email.com";

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> underTest.findByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with provided email not found");
    }

    @Test
    void GivenExistingUserEmail_WhenFindByEmail_ThenReturnsCorrectUser() {
        // Given
        String email = "i-do-not-exist@email.com";

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        // When
        User result = underTest.findByEmail(email);

        // Then
        assertThat(result).isEqualTo(user);
    }

}
