package com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;

public record WriteVideoDTO(
        @Null
        @Schema(hidden = true)
        UserDTO user,
        @JsonProperty("youtubeId")
        @NotBlank
        String youtubeId
) {
}
