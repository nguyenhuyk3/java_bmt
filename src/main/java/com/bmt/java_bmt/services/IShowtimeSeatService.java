package com.bmt.java_bmt.services;

import java.util.List;
import java.util.UUID;

import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeSeatResponse;

public interface IShowtimeSeatService {
    List<GetShowtimeSeatResponse> getShowtimeSeatsByShowtimeId(UUID id);
}
