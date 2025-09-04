package com.bmt.java_bmt.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.others.FilmDocument;
import com.bmt.java_bmt.dto.requests.favoriteFilm.ModifyFavoriteFilmListRequest;
import com.bmt.java_bmt.services.IFavoriteFilmService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/favorite-film")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteFilmController {
    IFavoriteFilmService favoriteFilmService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public APIResponse<String> modifyFavoriteFilmList(@ModelAttribute @Valid ModifyFavoriteFilmListRequest request) {
        var result = favoriteFilmService.modifyFavoriteFilmList(request.getFilmId());

        return APIResponse.<String>builder().result(result).build();
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public APIResponse<List<FilmDocument>> getFavoriteFilmList() {
        var result = favoriteFilmService.getFavoriteFilmList();

        return APIResponse.<List<FilmDocument>>builder().result(result).build();
    }
}
