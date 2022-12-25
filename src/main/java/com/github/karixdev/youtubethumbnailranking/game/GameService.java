package com.github.karixdev.youtubethumbnailranking.game;

import com.github.karixdev.youtubethumbnailranking.rating.RatingService;
import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumnail.Thumbnail;
import com.github.karixdev.youtubethumbnailranking.thumnail.ThumbnailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private final ThumbnailService thumbnailService;

    public Object start(UserPrincipal userPrincipal) {
//        Thumbnail thumbnail1 = thumbnailService.getRandom();

        return null;
    }
}
