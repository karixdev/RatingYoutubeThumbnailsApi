package com.github.karixdev.youtubethumbnailranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class YoutubeThumbnailRankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoutubeThumbnailRankingApplication.class, args);
	}

}
