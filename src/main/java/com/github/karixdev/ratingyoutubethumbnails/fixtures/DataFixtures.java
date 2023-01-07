package com.github.karixdev.ratingyoutubethumbnails.fixtures;

import com.github.karixdev.ratingyoutubethumbnails.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnails.thumbnail.ThumbnailRepository;
import com.github.karixdev.ratingyoutubethumbnails.user.User;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRepository;
import com.github.karixdev.ratingyoutubethumbnails.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnails.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataFixtures implements CommandLineRunner {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ThumbnailRepository thumbnailRepository;
    private final FixturesProperties properties;

    @Override
    public void run(String... args) {
        if (!properties.getLoadFixtures()) {
            return;
        }

        if (userRepository.findByEmail("admin@admin.com").isPresent() ||
                userRepository.findByUsername("admin").isPresent()
        ) {
            return;
        }

        User user = userService.createUser(
                "admin@admin.com",
                "admin",
                "admin123",
                UserRole.ROLE_ADMIN,
                true
        );

        List<Map<String, String>> thumbnails = List.of(
                Map.of(
                        "youtubeVideoId", "e-fA-gBCkj0",
                        "url", "https://i.ytimg.com/vi/e-fA-gBCkj0/maxresdefault.jpg"
                ),
                Map.of(
                        "youtubeVideoId", "dvgZkm1xWPE",
                        "url", "https://i.ytimg.com/vi/dvgZkm1xWPE/maxresdefault.jpg"
                ),
                Map.of(
                        "youtubeVideoId", "O0lf_fE3HwA",
                        "url", "https://i.ytimg.com/vi/O0lf_fE3HwA/maxresdefault.jpg"
                ),
                Map.of(
                        "youtubeVideoId", "SSCzDykng4g",
                        "url", "https://i.ytimg.com/vi/SSCzDykng4g/maxresdefault.jpg"
                ),
                Map.of(
                        "youtubeVideoId", "oxqnFJ3lp5k",
                        "url", "https://i.ytimg.com/vi/oxqnFJ3lp5k/maxresdefault.jpg"
                ),
                Map.of(
                        "youtubeVideoId", "psuRGfAaju4",
                        "url", "https://i.ytimg.com/vi/psuRGfAaju4/maxresdefault.jpg"
                ),
                Map.of(
                        "youtubeVideoId", "hT_nvWreIhg",
                        "url", "https://i.ytimg.com/vi/hT_nvWreIhg/maxresdefault.jpg"
                )
        );

        thumbnails.forEach(map -> {
            if (thumbnailRepository.findByYoutubeVideoId(
                    map.get("youtubeVideoId")).isEmpty()
            ) {
                thumbnailRepository.save(
                        Thumbnail.builder()
                                .youtubeVideoId(map.get("youtubeVideoId"))
                                .url(map.get("url"))
                                .addedBy(user)
                                .build()
                );
            }
        });
    }
}
