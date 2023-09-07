package com.github.karixdev.ratingyoutubethumbnailsapi.user.service;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.NewUserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserServiceApi;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.exception.UnavailableUserEmailException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.exception.UnavailableUserUsernameException;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.mapper.UserMapper;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceApi, UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Transactional
    @Override
    public UserDTO create(NewUserDTO dto) {
        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new UnavailableUserEmailException();
        }
        if (repository.findByUsername(dto.username()).isPresent()) {
            throw new UnavailableUserUsernameException();
        }

        User user = User.builder()
                .email(dto.email())
                .username(dto.username())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .build();

        repository.save(user);

        return mapper.userToDTO(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .map(mapper::userToDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User with provided email not found"));
    }
}
