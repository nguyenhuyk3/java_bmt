package com.bmt.java_bmt.services;

import com.bmt.java_bmt.dto.requests.film.CreateFilmRequest;
import com.bmt.java_bmt.dto.requests.film.UpdateFilmRequest;
import com.bmt.java_bmt.dto.responses.film.CreateFilmResponse;

public interface IFilmService {
    CreateFilmResponse createFilm(CreateFilmRequest request);

    CreateFilmResponse updateFilm(UpdateFilmRequest request);
}
