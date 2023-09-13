package com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

public record WriteVideoDTO(
        @Null
        UserDTO user,
        @JsonProperty("youtubeId")
        @NotBlank
        String youtubeId
) {
}
