package com.bmt.java_bmt.dto.responses.showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ReleasedShowtimeProjection {
    UUID getShowtimeId();

    LocalDate getShowDate();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();
}
