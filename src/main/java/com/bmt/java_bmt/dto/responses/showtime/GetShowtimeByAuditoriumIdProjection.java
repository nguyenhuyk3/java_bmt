package com.bmt.java_bmt.dto.responses.showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface GetShowtimeByAuditoriumIdProjection {
    UUID getShowtimeId();

    LocalDate getShowDate();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();

    Integer getCoefficient();
}
