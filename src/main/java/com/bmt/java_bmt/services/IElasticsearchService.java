package com.bmt.java_bmt.services;

import java.util.List;

import com.bmt.java_bmt.dto.others.FilmDocument;
import com.bmt.java_bmt.dto.requests.search.SearchRequest;

public interface IElasticsearchService {
    //    List<FilmDocument> searchFilms(String userQuery, int from, int size) throws IOException;
    List<FilmDocument> searchFilms(SearchRequest request);

    void indexFilm(FilmDocument filmDocument);

    void deleteFilm(String filmId);
}
