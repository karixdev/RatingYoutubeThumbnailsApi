package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.config;

import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.client.YoutubeApiClient;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.exception.YoutubeApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class YoutubeClientConfig {
    @Bean
    YoutubeApiClient youtubeApiClient(@Value("${youtube-api.base-url}") String baseUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::isError, resp -> resp.bodyToMono(String.class).flatMap(error -> {
                    log.error("Youtube API returned error status: {}; with body: \n{}", resp.statusCode().value(), error);
                    return Mono.error(new YoutubeApiException());
                }))
                .build();

        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builder(
                        WebClientAdapter.forClient(webClient)
                ).build();

        return factory.createClient(YoutubeApiClient.class);
    }
}
