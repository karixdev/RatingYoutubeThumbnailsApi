package com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail;

import com.github.karixdev.ratingyoutubethumbnailsapi.security.UserPrincipal;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.PermissionDeniedException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ResourceNotFoundException;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.payload.response.SuccessResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.exception.EmptyThumbnailsListException;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.exception.ThumbnailAlreadyExistsException;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.request.ThumbnailRequest;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.response.ThumbnailResponse;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.YoutubeVideoService;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.request.ItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ThumbnailService {
    private final ThumbnailRepository repository;
    private final YoutubeVideoService youtubeVideoService;

    @Transactional
    public ThumbnailResponse add(ThumbnailRequest payload, UserPrincipal userPrincipal) {
        String youtubeVideoId = payload.getYoutubeVideoId();

        if (repository.findByYoutubeVideoId(youtubeVideoId).isPresent()) {
            throw new ThumbnailAlreadyExistsException();
        }

        ItemRequest videoDetails = youtubeVideoService.getVideoDetails(youtubeVideoId);
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

    @Transactional
    public SuccessResponse delete(Long id, UserPrincipal userPrincipal) {
        Thumbnail thumbnail = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Thumbnail with provided id does not found"));

        User user = userPrincipal.getUser();

        if (!thumbnail.getAddedBy().equals(user) &&
                user.getUserRole() != UserRole.ROLE_ADMIN) {
            throw new PermissionDeniedException(
                    "Cannot delete thumbnail with provided id");
        }

        repository.delete(thumbnail);

        return new SuccessResponse();
    }

    public Thumbnail getRandomThumbnailFromList(List<Thumbnail> thumbnails) {
        if (thumbnails.isEmpty()) {
            throw new EmptyThumbnailsListException();
        }

        Random random = new Random();
        int randomIdx = random.nextInt(thumbnails.size());

        return thumbnails.get(randomIdx);
    }

    public Thumbnail getRandomThumbnail() {
        List<Thumbnail> thumbnails = repository.findAll();

        return getRandomThumbnailFromList(thumbnails);
    }

    public List<Thumbnail> getThumbnailsWithoutUserRating(User user) {
        return repository.findAllThumbnails().stream().filter(thumbnail ->
                thumbnail.getRatings().stream().noneMatch(rating -> rating.getUser().equals(user)))
                .toList();
    }

    public Thumbnail getThumbnailByYoutubeVideoId(String youtubeVideoId) {
        return repository.findByYoutubeVideoId(youtubeVideoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Thumbnail with provided youtube id not found"));
    }
}
