package com.github.karixdev.ratingyoutubethumbnails.auth.payload.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RegisterRequestTest {
    @Autowired
    JacksonTester<RegisterRequest> jTester;

    @Test
    void testDeserialize() throws IOException {
        String content = """
                {
                    "email": "abc@abc.pl",
                    "username": "username",
                    "password": "secret-password"
                }
                """;

        RegisterRequest result = jTester.parseObject(content);

        assertThat(result.getEmail()).isEqualTo("abc@abc.pl");
        assertThat(result.getUsername()).isEqualTo("username");
        assertThat(result.getPassword()).isEqualTo("secret-password");
    }
}
