package com.github.karixdev.ratingyoutubethumbnailsapi.game.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.karixdev.ratingyoutubethumbnailsapi.game.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.round.Round;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.payload.response.ThumbnailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResponse {
    @JsonProperty("id")
    Long id;

    @JsonProperty("thumbnails")
    @JsonIgnoreProperties({"youtube_video_id", "added_by"})
    List<ThumbnailResponse> thumbnails;

    public GameResponse(Game game) {
        Round latestRound = game.getLatestRound();

        this.id = game.getId();
        this.thumbnails = List.of(
                new ThumbnailResponse(latestRound.getThumbnail1()),
                new ThumbnailResponse(latestRound.getThumbnail2())
        );
    }
}
