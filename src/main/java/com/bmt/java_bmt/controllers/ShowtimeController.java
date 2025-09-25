package com.bmt.java_bmt.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.showtime.AddShowtimeRequest;
import com.bmt.java_bmt.dto.requests.showtime.ReleaseShowtimeRequest;
import com.bmt.java_bmt.dto.responses.showtime.AddShowtimeResponse;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeByAuditoriumIdAndShowDateResponse;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeByAuditoriumIdProjection;
import com.bmt.java_bmt.dto.responses.showtime.ReleasedShowtimeProjection;
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

    @GetMapping("/lastest")
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<LocalDateTime> getLatestShowtime(
            @RequestParam UUID auditoriumId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {
        var result = showtimeService.getLastestShowtimeByAuditoriumIdAndByShowDate(auditoriumId, showDate);

        return APIResponse.<LocalDateTime>builder().result(result).build();
    }

    @GetMapping("/find-showtimes-by-auditorium-id-and-by-showdate")
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<List<GetShowtimeByAuditoriumIdAndShowDateResponse>> findShowtimesByAuditoriumIdAndShowDate(
            @RequestParam UUID auditoriumId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {

        var result = showtimeService.findShowtimesByAuditoriumIdAndShowDate(auditoriumId, showDate);

        return APIResponse.<List<GetShowtimeByAuditoriumIdAndShowDateResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/find-all-released-showtimes-from-today")
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<List<ReleasedShowtimeProjection>> findAllReleasedShowtimesFromToday() {
        var result = showtimeService.findAllReleasedShowtimesFromToday();

        return APIResponse.<List<ReleasedShowtimeProjection>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/find-all-realesed-showtimes-by-auditorium-id")
    @PreAuthorize("hasRole('MANAGER')")
    public APIResponse<List<GetShowtimeByAuditoriumIdProjection>> findShowtimesByAuditoriumIdAndShowDate(
            @RequestParam UUID auditoriumId) {
        var result = showtimeService.findAllReleasedShowtimesByAuditoriumId(auditoriumId);

        return APIResponse.<List<GetShowtimeByAuditoriumIdProjection>>builder()
                .result(result)
                .build();
    }
}
