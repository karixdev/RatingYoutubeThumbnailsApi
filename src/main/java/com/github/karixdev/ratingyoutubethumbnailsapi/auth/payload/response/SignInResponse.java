package com.github.karixdev.ratingyoutubethumbnailsapi.auth.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.payload.repsonse.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("user")
    private UserResponse userResponse;

    public SignInResponse(String accessToken, User user) {
        this.accessToken = accessToken;
        this.userResponse = new UserResponse(user);
    }
}
