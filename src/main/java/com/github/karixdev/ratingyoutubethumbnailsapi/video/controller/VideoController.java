package com.github.karixdev.ratingyoutubethumbnailsapi.video.controller;

import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.CurrentUser;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.VideoServiceApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoServiceApi videoService;

    @PostMapping
    ResponseEntity<VideoDTO> create(
            @RequestBody @Valid WriteVideoDTO request,
            @CurrentUser UserDTO userDTO
    ) {
        return new ResponseEntity<>(
                videoService.create(new WriteVideoDTO(userDTO, request.youtubeId())),
                HttpStatus.CREATED
        );
    }
}
