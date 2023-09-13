package com.github.karixdev.ratingyoutubethumbnailsapi.video.mapper;


import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.VideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.video.WriteVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.youtube.YoutubeVideoDTO;
import com.github.karixdev.ratingyoutubethumbnailsapi.video.entity.Video;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @Mappings({
            @Mapping(
                    source = "youtubeVideoDTO.defaultResUrl",
                    target = "defaultResThumbnail"
            ),
            @Mapping(
                    source = "youtubeVideoDTO.mediumResUrl",
                    target = "mediumResThumbnail"
            ),
            @Mapping(
                    source = "youtubeVideoDTO.highResUrl",
                    target = "highResThumbnail"
            ),
            @Mapping(
                    source = "youtubeVideoDTO.standardResUrl",
                    target = "standardResThumbnail"
            ),
            @Mapping(
                    source = "youtubeVideoDTO.maxResUrl",
                    target = "maxResThumbnail"
            ),
            @Mapping(
                    source = "writeVideoDTO.youtubeId",
                    target = "youtubeId"
            ),
            @Mapping(
                    source = "writeVideoDTO.user.id",
                    target = "userId"
            ),
            @Mapping(
                    target = "id",
                    ignore = true
            ),
            @Mapping(
                    target = "state",
                    ignore = true
            )
    })
    Video writeDtoAndYoutubeVideoToEntity(WriteVideoDTO writeVideoDTO, YoutubeVideoDTO youtubeVideoDTO);

    @Mappings({
            @Mapping(
                    source = "defaultResThumbnail",
                    target = "thumbnails.defaultRes"
            ),
            @Mapping(
                    source = "mediumResThumbnail",
                    target = "thumbnails.mediumRes"
            ),
            @Mapping(
                    source = "highResThumbnail",
                    target = "thumbnails.highRes"
            ),
            @Mapping(
                    source = "standardResThumbnail",
                    target = "thumbnails.standardRes"
            ),
            @Mapping(
                    source = "maxResThumbnail",
                    target = "thumbnails.maxRes"
            )
    })
    VideoDTO entityToDto(Video video);
}
