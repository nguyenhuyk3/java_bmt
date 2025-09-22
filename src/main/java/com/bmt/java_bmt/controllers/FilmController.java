package com.bmt.java_bmt.controllers;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.film.CreateFilmRequest;
import com.bmt.java_bmt.dto.requests.film.UpdateFilmRequest;
import com.bmt.java_bmt.dto.responses.film.CreateFilmResponse;
import com.bmt.java_bmt.dto.responses.film.GetFilmResponse;
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
        var result = filmService.createFilm(request);

        return APIResponse.<CreateFilmResponse>builder().result(result).build();
    }

    @PutMapping
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<CreateFilmResponse> updateFilm(@ModelAttribute @Valid UpdateFilmRequest request) {
        var result = filmService.updateFilm(request);

        return APIResponse.<CreateFilmResponse>builder().result(result).build();
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<Page<GetFilmResponse>> getFilms(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        var result = filmService.getAllFilms(page, size);

        return APIResponse.<Page<GetFilmResponse>>builder().result(result).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<GetFilmResponse> getFilmById(@PathVariable UUID id) {
        var result = filmService.getFilmById(id);

        return APIResponse.<GetFilmResponse>builder().result(result).build();
    }
}
