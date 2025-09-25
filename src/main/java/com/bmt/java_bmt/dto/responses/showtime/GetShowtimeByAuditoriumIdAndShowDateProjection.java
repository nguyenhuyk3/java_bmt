package com.bmt.java_bmt.dto.responses.showtime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface GetShowtimeByAuditoriumIdAndShowDateProjection {
    String getShowtimeId();

    LocalDate getShowDate();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();

    Integer getCoefficient();

    String getFilmId();

    String getFilmTitle();

    String getFilmDescription();

    LocalDate getFilmReleaseDate();

    LocalTime getFilmDuration();

    String getPosterUrl();

    String getTrailerUrl();

    String getGenres();

    String getFilmProfessionals();
}
