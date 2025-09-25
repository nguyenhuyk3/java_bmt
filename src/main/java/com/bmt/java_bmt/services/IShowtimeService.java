package com.bmt.java_bmt.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.bmt.java_bmt.dto.requests.showtime.AddShowtimeRequest;
import com.bmt.java_bmt.dto.requests.showtime.ReleaseShowtimeRequest;
import com.bmt.java_bmt.dto.responses.showtime.AddShowtimeResponse;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeByAuditoriumIdAndShowDateResponse;
import com.bmt.java_bmt.dto.responses.showtime.GetShowtimeByAuditoriumIdProjection;
import com.bmt.java_bmt.dto.responses.showtime.ReleasedShowtimeProjection;

public interface IShowtimeService {
    AddShowtimeResponse addShowtime(AddShowtimeRequest request);

    String releaseShowtime(ReleaseShowtimeRequest request);

    LocalDateTime getLastestShowtimeByAuditoriumIdAndByShowDate(UUID auditoriumId, LocalDate showDate);

    List<GetShowtimeByAuditoriumIdAndShowDateResponse> findShowtimesByAuditoriumIdAndShowDate(
            UUID auditoriumId, LocalDate showDate);

    List<ReleasedShowtimeProjection> findAllReleasedShowtimesFromToday();

    List<GetShowtimeByAuditoriumIdProjection> findAllReleasedShowtimesByAuditoriumId(UUID auditoriumId);
}
