package com.bmt.java_bmt.controllers;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.film.CreateFilmRequest;
import com.bmt.java_bmt.dto.requests.film.UpdateFilmRequest;
import com.bmt.java_bmt.dto.responses.film.CreateFilmResponse;
import com.bmt.java_bmt.services.IFilmService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/film")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    IFilmService filmService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<CreateFilmResponse> createFilm(@ModelAttribute @Valid CreateFilmRequest request) {
        CreateFilmResponse createFilmResponse = filmService.createFilm(request);

        return APIResponse.<CreateFilmResponse>builder()
                .result(createFilmResponse)
                .build();
    }

    @PutMapping
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<CreateFilmResponse> updateFilm(@ModelAttribute @Valid UpdateFilmRequest request) {
        CreateFilmResponse createFilmResponse = filmService.updateFilm(request);

        return APIResponse.<CreateFilmResponse>builder()
                .result(createFilmResponse)
                .build();
    }
}
