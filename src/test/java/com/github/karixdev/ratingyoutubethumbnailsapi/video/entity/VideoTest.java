package com.github.karixdev.ratingyoutubethumbnailsapi.video.entity;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.entity.EntityState;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.exception.ForbiddenActionException;
import com.github.karixdev.ratingyoutubethumbnailsapi.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VideoTest {

    @Test
    void GivenUserWhoIsNotAdminNorOwnerOfVideo_WhenMoveIntoRemovedState_ThenThrowsForbiddenActionException() {
        // Given
        UserDTO userDTO = TestUtils.createUserDTO();
        Video video = TestUtils.createVideo();

        // When & Then
        assertThatThrownBy(() -> video.moveIntoRemovedState(userDTO))
                .isInstanceOf(ForbiddenActionException.class);
    }

    @Test
    void GivenOwnerOfVideo_WhenMoveIntoRemovedState_ThenVideoIsMovedToRemovedState() {
        // Given
        UserDTO userDTO = TestUtils.createUserDTO();
        Video video = TestUtils.createVideo(UUID.randomUUID(), "yt-id", userDTO.id(), LocalDateTime.now());

        // When
        video.moveIntoRemovedState(userDTO);

        // Then
        assertThat(video.getState()).isEqualTo(EntityState.REMOVED);
    }

    @Test
    void GivenAdminUserWhoIsNotOwnerOfVideo_WhenMoveIntoRemovedState_ThenVideoIsMovedToRemovedState() {
        // Given
        UserDTO userDTO = TestUtils.createAdminUserDTO();
        Video video = TestUtils.createVideo();

        // When
        video.moveIntoRemovedState(userDTO);

        // Then
        assertThat(video.getState()).isEqualTo(EntityState.REMOVED);
    }

}