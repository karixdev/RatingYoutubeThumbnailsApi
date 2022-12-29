package com.github.karixdev.ratingyoutubethumbnails.emailverification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
public class EmailVerificationPropertiesTest {
    @Autowired
    EmailVerificationProperties underTest;

    @Test
    void shouldLoadTokenExpirationHours() {
        assertThat(underTest.getTokenExpirationHours()).isNotNull();
    }

    @Test
    void shouldLoadMaxNumberOfMailsPerHour() {
        assertThat(underTest.getMaxNumberOfMailsPerHour()).isNotNull();
    }
}
