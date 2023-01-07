package com.github.karixdev.ratingyoutubethumbnailsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RatingYoutubeThumbnailsApi {

	public static void main(String[] args) {
		SpringApplication.run(RatingYoutubeThumbnailsApi.class, args);
	}

}
