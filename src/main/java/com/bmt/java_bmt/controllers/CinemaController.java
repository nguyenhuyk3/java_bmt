package com.bmt.java_bmt.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.responses.cinema.GetCinemaResponse;
import com.bmt.java_bmt.services.ICinemaService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/cinema")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CinemaController {
    ICinemaService cinemaService;

    @GetMapping("/get-all")
    public APIResponse<List<GetCinemaResponse>> getAllCinemas() {
        var result = cinemaService.getAllCinemas();

        return APIResponse.<List<GetCinemaResponse>>builder().result(result).build();
    }
}
