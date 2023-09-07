package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.karixdev.ratingyoutubethumbnailsapi.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class JwtServiceIT extends ContainersEnvironment {
    @Autowired
    JwtService underTest;

    @Test
    void shouldCreateJWT() {
        UserDTO userDTO = createUserDTO();

        String jwt = underTest.create(userDTO);
        assertThat(jwt).isNotNull();
    }

    @Test
    void shouldVerifyCreatedToken() {
        UserDTO userDTO = createUserDTO();

        String jwt = underTest.create(userDTO);
        DecodedJWT decoded = underTest.verify(jwt);

        assertThat(decoded).isNotNull();
        assertThat(decoded.getIssuer()).isEqualTo(JwtProperties.ISSUER);
    }

    @Test
    void shouldGetEmailFromToken() {
        UserDTO userDTO = createUserDTO();

        String jwt = underTest.create(userDTO);
        DecodedJWT decodedJWT = underTest.verify(jwt);

        String email = underTest.getEmailFromToken(decodedJWT);
        assertThat(email).isEqualTo(userDTO.email());
    }

    private static UserDTO createUserDTO() {
        return new UserDTO(
                UUID.randomUUID(),
                "email@email.com",
                "username",
                UserRole.USER,
                "password"
        );
    }
}