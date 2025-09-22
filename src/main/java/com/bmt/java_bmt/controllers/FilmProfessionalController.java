package com.bmt.java_bmt.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.responses.filmProfessional.GetFilmProfessionalResponse;
import com.bmt.java_bmt.services.IFilmProfessionalService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/film-professionals")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmProfessionalController {
    IFilmProfessionalService filmProfessionalService;

    @GetMapping()
    APIResponse<List<GetFilmProfessionalResponse>> getAll() {
        var result = filmProfessionalService.getAll();

        return APIResponse.<List<GetFilmProfessionalResponse>>builder()
                .result(result)
                .build();
    }
}
