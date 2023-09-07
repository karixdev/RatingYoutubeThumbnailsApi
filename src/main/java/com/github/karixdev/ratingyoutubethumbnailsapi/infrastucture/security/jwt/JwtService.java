package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt.exception.InvalidJwtException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final Clock clock;
    private final JWTVerifier jwtVerifier;
    private final Algorithm algorithm;

    public String create(UserDTO userDTO) {
        Instant expiresAt = Instant.now(clock)
                .plus(JwtProperties.TTL_HOURS, ChronoUnit.HOURS);

        return JWT.create()
                .withIssuer(JwtProperties.ISSUER)
                .withSubject(userDTO.email())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        try {
            return jwtVerifier.verify(token);
        } catch (JWTVerificationException ex) {
            log.error("Error while token JWT verification", ex);
            throw new InvalidJwtException();
        }
    }

    public String getEmailFromToken(DecodedJWT jwt) {
        String subject = jwt.getSubject();

        if (subject == null) {
            throw new InvalidJwtException();
        }

        return subject;
    }
}
