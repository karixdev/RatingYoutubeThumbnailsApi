package com.github.karixdev.youtubethumbnailranking.user.payload.repsonse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.karixdev.youtubethumbnailranking.user.User;
import com.github.karixdev.youtubethumbnailranking.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @JsonProperty("user_role")
    private UserRole userRole;

    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    public UserResponse(User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.userRole = user.getUserRole();
        this.isEnabled = user.getIsEnabled();
    }
}
