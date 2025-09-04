package com.bmt.java_bmt.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.others.FilmDocument;
import com.bmt.java_bmt.dto.requests.search.SearchRequest;
import com.bmt.java_bmt.services.IElasticsearchService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchController {
    IElasticsearchService searchService;

    @PostMapping()
    public APIResponse<List<FilmDocument>> searchFilms(@RequestBody SearchRequest request) {
        var films = searchService.searchFilms(request);

        return APIResponse.<List<FilmDocument>>builder().result(films).build();
    }
}
