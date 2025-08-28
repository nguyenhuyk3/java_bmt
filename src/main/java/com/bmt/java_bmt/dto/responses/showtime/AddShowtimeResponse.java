package com.bmt.java_bmt.dto.responses.showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddShowtimeResponse {
    UUID id;
    Integer coefficient;
    LocalDate showDate;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Boolean isReleased;
    UUID auditoriumId;
    UUID filmId;
}
