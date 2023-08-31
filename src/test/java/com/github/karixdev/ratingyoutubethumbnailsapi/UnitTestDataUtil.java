package com.github.karixdev.ratingyoutubethumbnailsapi;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.thumbnail.Thumbnail;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.User;
import com.github.karixdev.ratingyoutubethumbnailsapi.user.UserRole;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UnitTestDataUtil {
    public static ZonedDateTime createZonedDateTime() {
        return ZonedDateTime.of(
                2022,
                11,
                23,
                13,
                44,
                30,
                0,
                ZoneId.of("UTC+0")
        );
    }
}
