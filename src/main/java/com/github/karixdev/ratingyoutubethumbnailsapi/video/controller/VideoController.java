package com.github.karixdev.ratingyoutubethumbnailsapi.video.controller;

import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ErrorDetails;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.exception.handler.payload.ValidationErrorDetails;
import com.github.karixdev.ratingyoutubethumbnailsapi.infrastucture.security.CurrentUser;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.VideoServiceApi;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Videos")
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoServiceApi videoService;

    @Operation(
            summary = "Add new video",
            security = {@SecurityRequirement(name = "bearer")}
    )
    @ApiResponse(
            responseCode = "201",
            description = "Successfully added video",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VideoDTO.class)
                    )
            }
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorDetails.class)
                    )
            }
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )
            }
    )
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

    @Operation(
            summary = "Delete video by id",
            security = {@SecurityRequirement(name = "bearer")},
            description = "Delete video by id. Deletion is not done directly - firstly video is moved into removed state and then scheduled job deletes videos that are not in any most recent round and are in removed state."
    )
    @ApiResponse(
            responseCode = "204",
            description = "Successfully removed video",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VideoDTO.class)
                    )
            }
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorDetails.class)
                    )
            }
    )
    @ApiResponse(
            responseCode = "404",
            description = "Video not found",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )
            }
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )
            }
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDetails.class)
                    )
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @PathVariable("id") UUID id,
            @CurrentUser UserDTO user
    ) {
        videoService.delete(id, user);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
