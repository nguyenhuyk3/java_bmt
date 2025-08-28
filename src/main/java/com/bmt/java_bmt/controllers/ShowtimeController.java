package com.bmt.java_bmt.controllers;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.showtime.AddShowtimeRequest;
import com.bmt.java_bmt.dto.requests.showtime.ReleaseShowtimeRequest;
import com.bmt.java_bmt.dto.responses.showtime.AddShowtimeResponse;
import com.bmt.java_bmt.services.IShowtimeService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/showtime")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeController {
    IShowtimeService showtimeService;

    @PostMapping()
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<AddShowtimeResponse> addShowtime(@RequestBody @Valid AddShowtimeRequest request) {
        var result = showtimeService.addShowtime(request);

        return APIResponse.<AddShowtimeResponse>builder().result(result).build();
    }

    @PostMapping("/release")
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<String> releaseShowtime(@RequestBody @Valid ReleaseShowtimeRequest request) {
        var result = showtimeService.releaseShowtime(request);

        return APIResponse.<String>builder().result(result).build();
    }
}
