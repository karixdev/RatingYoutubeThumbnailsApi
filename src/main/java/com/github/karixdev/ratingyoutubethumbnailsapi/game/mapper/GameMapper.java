package com.github.karixdev.ratingyoutubethumbnailsapi.game.mapper;

import com.github.karixdev.ratingyoutubethumbnailsapi.game.entity.Game;
import com.github.karixdev.ratingyoutubethumbnailsapi.shared.game.GameDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameDTO entityToDTO(Game game);
}
