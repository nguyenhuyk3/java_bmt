package com.bmt.java_bmt.services;

import com.bmt.java_bmt.dto.others.FilmDocument;

import java.io.IOException;
import java.util.List;

public interface ISearchService {
    List<FilmDocument> searchFilms(String userQuery, int from, int size) throws IOException;
}
