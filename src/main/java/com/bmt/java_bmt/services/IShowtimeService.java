package com.bmt.java_bmt.services;

import com.bmt.java_bmt.dto.requests.showtime.AddShowtimeRequest;
import com.bmt.java_bmt.dto.requests.showtime.ReleaseShowtimeRequest;
import com.bmt.java_bmt.dto.responses.showtime.AddShowtimeResponse;

public interface IShowtimeService {
    AddShowtimeResponse addShowtime(AddShowtimeRequest request);

    String releaseShowtime(ReleaseShowtimeRequest request);
}
