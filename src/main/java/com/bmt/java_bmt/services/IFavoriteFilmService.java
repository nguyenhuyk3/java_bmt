package com.bmt.java_bmt.services;

import java.util.List;
import java.util.UUID;

import com.bmt.java_bmt.dto.others.FilmDocument;

public interface IFavoriteFilmService {
    String modifyFavoriteFilmList(UUID filmId);

    List<FilmDocument> getFavoriteFilmList();
}
