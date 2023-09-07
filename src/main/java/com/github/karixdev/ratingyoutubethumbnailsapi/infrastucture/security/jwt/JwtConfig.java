package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Bean
    Algorithm algorithm(@Value("${jwt.secret}") String secret) {
        return Algorithm.HMAC256(secret);
    }

    @Bean
    JWTVerifier jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm)
                .withIssuer(JwtProperties.ISSUER)
                .build();
    }
}
