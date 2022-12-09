package com.github.karixdev.youtubethumbnailranking.user;

import com.github.karixdev.youtubethumbnailranking.user.exception.EmailNotAvailableException;
import com.github.karixdev.youtubethumbnailranking.user.exception.UsernameNotAvailableException;
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
    public User createUser(String email, String username, String plainPassword, UserRole userRole) {
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
                .build();

        return repository.save(user);
    }
}
