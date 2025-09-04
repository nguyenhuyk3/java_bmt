package com.bmt.java_bmt.dto.requests.favoriteFilm;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModifyFavoriteFilmListRequest {
    @NotNull(message = "Id của phim không được để trống")
    UUID filmId;
}
