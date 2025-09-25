package com.bmt.java_bmt.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.bmt.java_bmt.dto.requests.film.CreateFilmRequest;
import com.bmt.java_bmt.dto.requests.film.UpdateFilmRequest;
import com.bmt.java_bmt.dto.responses.film.CreateFilmResponse;
import com.bmt.java_bmt.dto.responses.film.GetFilmResponse;

public interface IFilmService {
    CreateFilmResponse createFilm(CreateFilmRequest request);

    CreateFilmResponse updateFilm(UpdateFilmRequest request);

    Page<GetFilmResponse> getFilms(int page, int size);

    List<GetFilmResponse> getAllFilms();

    GetFilmResponse getFilmById(UUID id);
}
