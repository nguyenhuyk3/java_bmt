package com.bmt.java_bmt.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeSeatResponse;
import com.bmt.java_bmt.services.IShowtimeSeatService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/showtime-seat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeSeatController {
    IShowtimeSeatService showtimeSeatService;

    @GetMapping()
    public APIResponse<List<GetShowtimeSeatResponse>> getShowtimeSeatsByShowtimeId(
            @RequestParam(required = true) UUID showtimeId) {
        var result = showtimeSeatService.getShowtimeSeatsByShowtimeId(showtimeId);

        return APIResponse.<List<GetShowtimeSeatResponse>>builder()
                .result(result)
                .build();
    }
}
