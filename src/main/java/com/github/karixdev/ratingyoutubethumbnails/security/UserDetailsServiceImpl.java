package com.github.karixdev.ratingyoutubethumbnails.security;

import com.github.karixdev.ratingyoutubethumbnails.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnails.user.User;
import com.github.karixdev.ratingyoutubethumbnails.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userService.findByEmail(email);
            return new UserPrincipal(user);
        } catch (ResourceNotFoundException e) {
            throw new UsernameNotFoundException("User with provided email not found");
        }
    }
}