package com.github.karixdev.ratingyoutubethumbnailsapi.it.template;

import com.github.karixdev.ratingyoutubethumbnailsapi.it.ContainersEnvironment;
import com.github.karixdev.ratingyoutubethumbnailsapi.template.TemplateProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ThymeleafTemplateProviderTest extends ContainersEnvironment {
    @Autowired
    @Qualifier("thymeleafTemplateProvider")
    TemplateProvider underTest;

    @Test
    void shouldLoadTemplateWithVariables() {
        Map<String, Object> variables = Map.of("name", "test");

        String result = underTest.getTemplate("test.html", variables);

        assertThat(result).isEqualTo("<p>test</p>");
    }
}