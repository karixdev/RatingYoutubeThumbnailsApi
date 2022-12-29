package com.github.karixdev.ratingyoutubethumbnails;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class RatingYoutubeThumbnails {

	public static void main(String[] args) {
		SpringApplication.run(RatingYoutubeThumbnails.class, args);
	}

}
