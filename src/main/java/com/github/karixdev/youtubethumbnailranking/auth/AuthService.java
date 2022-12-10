package com.github.karixdev.youtubethumbnailranking.auth;

import com.github.karixdev.youtubethumbnailranking.auth.payload.request.RegisterRequest;
import com.github.karixdev.youtubethumbnailranking.emailverification.EmailVerificationService;
import com.github.karixdev.youtubethumbnailranking.emailverification.EmailVerificationToken;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import com.github.karixdev.youtubethumbnailranking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public SuccessResponse registerNewUser(RegisterRequest payload) {
        User user = userService.createUser(
                payload.getEmail(),
                payload.getUsername(),
                payload.getPassword(),
                UserRole.ROLE_USER,
                Boolean.FALSE
        );

        EmailVerificationToken token =
                emailVerificationService.createToken(user);

        emailVerificationService.sendEmailWithVerificationLink(token);

        return new SuccessResponse();
    }

}
