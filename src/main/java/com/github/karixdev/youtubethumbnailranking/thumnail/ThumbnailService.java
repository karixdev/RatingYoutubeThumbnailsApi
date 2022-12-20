package com.github.karixdev.youtubethumbnailranking.thumnail;

import com.github.karixdev.youtubethumbnailranking.security.UserPrincipal;
import com.github.karixdev.youtubethumbnailranking.thumnail.exception.ThumbnailAlreadyExistsException;
import com.github.karixdev.youtubethumbnailranking.thumnail.payload.request.ThumbnailRequest;
import com.github.karixdev.youtubethumbnailranking.thumnail.payload.response.ThumbnailResponse;
import com.github.karixdev.youtubethumbnailranking.youtube.YoutubeVideoService;
import com.github.karixdev.youtubethumbnailranking.youtube.payload.response.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThumbnailService {
    private final ThumbnailRepository repository;
    private final YoutubeVideoService youtubeVideoService;

    public ThumbnailResponse add(ThumbnailRequest payload, UserPrincipal userPrincipal) {
        String youtubeVideoId = payload.getYoutubeVideoId();

        if (repository.findByYoutubeVideoId(youtubeVideoId).isPresent()) {
            throw new ThumbnailAlreadyExistsException();
        }

        Item videoDetails = youtubeVideoService.getVideoDetails(youtubeVideoId);
        String maxresThumbnailUrl = videoDetails.getSnippet().getThumbnails()
                .getMaxres()
                .getUrl();

        Thumbnail thumbnail = Thumbnail.builder()
                .youtubeVideoId(youtubeVideoId)
                .url(maxresThumbnailUrl)
                .addedBy(userPrincipal.getUser())
                .build();

        thumbnail = repository.save(thumbnail);

        return new ThumbnailResponse(thumbnail);
    }
}
