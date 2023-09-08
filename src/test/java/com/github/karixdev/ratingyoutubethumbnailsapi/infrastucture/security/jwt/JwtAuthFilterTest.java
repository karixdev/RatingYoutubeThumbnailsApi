package com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {
    @InjectMocks
    JwtAuthFilter underTest;

    @Mock
    JwtService jwtService;

    @Mock
    UserDetailsService userDetailsService;

    @Mock
    HttpServletResponse servletResponse;

    @Mock
    FilterChain filterChain;

    @Mock
    DecodedJWT decodedJWT;

    @Mock
    SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void GivenRequestWithoutAuthorizationHeader_WhenDoFilterInternal_ThenAuthenticationIsNotProcessed() throws Exception {
        // Given
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();

        // When
        underTest.doFilterInternal(servletRequest, servletResponse, filterChain);

        // Then
        verify(userDetailsService, never()).loadUserByUsername(any(String.class));
        verify(filterChain).doFilter(
                any(ServletRequest.class),
                any(ServletResponse.class));
    }

    @Test
    void GivenRequestWithValidToken_WhenDoFilterInternal_ThenAuthenticationIsProcessed() throws Exception {
        // Given
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("Authorization", "Bearer token");

        when(jwtService.verify(eq("token")))
                .thenReturn(decodedJWT);

        when(jwtService.getEmailFromToken(decodedJWT))
                .thenReturn("email@email.com");

        UserDTO userDetails = new UserDTO(
                UUID.randomUUID(),
                "email@email.com",
                "username",
                UserRole.USER,
                "password"
        );

        when(userDetailsService.loadUserByUsername(eq("email@email.com")))
                .thenReturn(userDetails);

        // When
        underTest.doFilterInternal(servletRequest, servletResponse, filterChain);

        // Then
        verify(userDetailsService).loadUserByUsername(eq("email@email.com"));
        verify(SecurityContextHolder.getContext()).setAuthentication(any(Authentication.class));
        verify(filterChain).doFilter(
                eq(servletRequest),
                any(ServletResponse.class));
    }
}