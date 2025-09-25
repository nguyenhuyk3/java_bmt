package com.bmt.java_bmt.dto.responses.showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bmt.java_bmt.dto.responses.film.GetFilmResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetShowtimeByAuditoriumIdAndShowDateResponse {
    private UUID showtimeId;
    private LocalDate showDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer coefficient;
    private GetFilmResponse film;
}
