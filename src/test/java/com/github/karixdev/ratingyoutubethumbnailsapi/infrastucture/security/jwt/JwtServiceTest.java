package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt.exception.InvalidJwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    JwtService underTest;

    @Mock
    JWTVerifier jwtVerifier;

    @Test
    void GivenInvalidJWT_WhenVerify_ThenThrowsInvalidJwtException() {
        // Given
        String jwt = "token";

        when(jwtVerifier.verify(eq(jwt)))
                .thenThrow(InvalidJwtException.class);

        // When & Then
        assertThatThrownBy(() -> underTest.verify(jwt))
                .isInstanceOf(InvalidJwtException.class);
    }

    @Test
    void GivenDecodedJWTWithoutSubject_WhenGetEmailFromToken_ThenThrowsInvalidJwtException() {
        // Given
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        when(decodedJWT.getSubject()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> underTest.getEmailFromToken(decodedJWT))
                .isInstanceOf(InvalidJwtException.class);
    }

    @Test
    void GivenDecodedJWTWithSubject_WhenGetEmailFromToken_ThenReturnsCorrectEmail() {
        // Given
        DecodedJWT decodedJWT = mock(DecodedJWT.class);

        String email = "email@email.com";
        when(decodedJWT.getSubject())
                .thenReturn(email);

        // When
        String result = underTest.getEmailFromToken(decodedJWT);

        // Then
        assertThat(result).isEqualTo(email);
    }
}