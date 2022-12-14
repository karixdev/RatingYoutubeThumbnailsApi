package com.github.karixdev.youtubethumbnailranking.auth;

import com.github.karixdev.youtubethumbnailranking.auth.payload.request.RegisterRequest;
import com.github.karixdev.youtubethumbnailranking.auth.payload.request.SignInRequest;
import com.github.karixdev.youtubethumbnailranking.auth.payload.response.SignInResponse;
import com.github.karixdev.youtubethumbnailranking.emailverification.EmailVerificationService;
import com.github.karixdev.youtubethumbnailranking.emailverification.EmailVerificationToken;
import com.github.karixdev.youtubethumbnailranking.jwt.JwtService;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.shared.payload.response.SuccessResponse;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import com.github.karixdev.youtubethumbnailranking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    public SignInResponse signIn(SignInRequest payload) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        payload.getEmail(), payload.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.createToken(userPrincipal);

        return new SignInResponse(token, userPrincipal.getUser());
    }
}
