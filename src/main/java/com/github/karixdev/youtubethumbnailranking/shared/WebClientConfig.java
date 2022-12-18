package com.github.karixdev.youtubethumbnailranking.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Configuration
public class WebClientConfig {
    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .build();
    }
}
