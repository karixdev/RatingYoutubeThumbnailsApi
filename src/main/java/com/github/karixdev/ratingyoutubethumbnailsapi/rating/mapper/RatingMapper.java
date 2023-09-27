package com.github.karixdev.ratingyoutubethumbnailsapi.rating.mapper;

import com.github.karixdev.ratingyoutubethumbnailsapi.rating.entity.Rating;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.dto.rating.RatingDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    RatingDTO entityToDTO(Rating rating);

}
