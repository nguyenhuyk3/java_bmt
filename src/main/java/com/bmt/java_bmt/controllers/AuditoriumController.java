package com.bmt.java_bmt.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.responses.auditorium.GetAuditoriumResponse;
import com.bmt.java_bmt.services.IAuditoriumService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auditorium")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditoriumController {
    IAuditoriumService auditoriumService;

    @GetMapping("/get-all-by-cinema-id/{cinemaId}")
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<List<GetAuditoriumResponse>> getAuditoriumsByCinemaId(@PathVariable UUID cinemaId) {
        var result = auditoriumService.getAllAuditoriumsByCinemaId(cinemaId);

        return APIResponse.<List<GetAuditoriumResponse>>builder().result(result).build();
    }
}
