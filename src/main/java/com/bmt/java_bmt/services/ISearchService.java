package com.bmt.java_bmt.services;

import java.io.IOException;
import java.util.List;

import com.bmt.java_bmt.dto.others.FilmDocument;

public interface ISearchService {
    List<FilmDocument> searchFilms(String userQuery, int from, int size) throws IOException;
}
