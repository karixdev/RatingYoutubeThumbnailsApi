package com.github.karixdev.ratingyoutubethumbnailsapi.user;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.exception.EmailNotAvailableException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.exception.UsernameNotAvailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public boolean isEmailAvailable(String email) {
        return repository.findByEmail(email).isEmpty();
    }

    public boolean isUsernameAvailable(String username) {
        return repository.findByUsername(username).isEmpty();
    }

    @Transactional
    public User createUser(String email, String username, String plainPassword, UserRole userRole, Boolean isEnabled) {
        if (!isEmailAvailable(email)) {
            throw new EmailNotAvailableException();
        }
        if (!isUsernameAvailable(username)) {
            throw new UsernameNotAvailableException();
        }

        String encodedPassword = passwordEncoder.encode(plainPassword);

        User user = User.builder()
                .email(email)
                .username(username)
                .password(encodedPassword)
                .userRole(userRole)
                .isEnabled(isEnabled)
                .build();

        return repository.save(user);
    }

    @Transactional
    public void enableUser(User user) {
        user.setIsEnabled(Boolean.TRUE);
        repository.save(user);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new ResourceNotFoundException(
                            "User with provided email not found");
                });
    }
}
