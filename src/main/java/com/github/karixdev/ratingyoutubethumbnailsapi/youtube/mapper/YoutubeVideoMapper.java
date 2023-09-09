package com.github.karixdev.ratingyoutubethumbnailsapi.youtube.mapper;

import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.youtube.payload.ItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface YoutubeVideoMapper {
    @Mappings(value = {
            @Mapping(
                    source = "snippet.thumbnails.defaultRes.url",
                    target = "defaultResUrl"
            ),
            @Mapping(
                    source = "snippet.thumbnails.mediumRes.url",
                    target = "mediumResUrl"
            ),
            @Mapping(
                    source = "snippet.thumbnails.highRes.url",
                    target = "highResUrl"
            ),
            @Mapping(
                    source = "snippet.thumbnails.standardRes.url",
                    target = "standardResUrl"
            ),
            @Mapping(
                    source = "snippet.thumbnails.maxRes.url",
                    target = "maxResUrl"
            ),
            @Mapping(
                    source = "id",
                    target = "id"
            )
    })
    YoutubeVideoDTO itemToDTO(ItemResponse item);
}
