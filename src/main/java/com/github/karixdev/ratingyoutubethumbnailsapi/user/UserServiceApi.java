package com.github.karixdev.ratingyoutubethumbnailsapi.user;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.NewUserDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.user.UserDTO;

public interface UserServiceApi {
    UserDTO create(NewUserDTO dto);
}
