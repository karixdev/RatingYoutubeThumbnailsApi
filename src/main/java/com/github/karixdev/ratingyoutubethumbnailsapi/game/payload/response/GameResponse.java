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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResponse {
    @JsonProperty("id")
    Long id;

    @JsonProperty("thumbnail1")
    @JsonIgnoreProperties({"youtube_video_id", "added_by"})
    ThumbnailResponse thumbnail1;

    @JsonProperty("thumbnail2")
    @JsonIgnoreProperties({"youtube_video_id", "added_by"})
    ThumbnailResponse thumbnail2;

    public GameResponse(Game game) {
        Round latestRound = game.getLatestRound();

        this.id = game.getId();
        this.thumbnail1 = new ThumbnailResponse(latestRound.getThumbnail1());
        this.thumbnail2 = new ThumbnailResponse(latestRound.getThumbnail2());
    }
}
