package com.bmt.java_bmt.dto.requests.showtime;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddShowtimeRequest {
    @NotNull(message = "ID phim không được để trống")
    UUID filmId;

    @NotNull(message = "ID phòng chiếu không được để trống")
    UUID auditoriumId;

    @NotNull(message = "Ngày chiếu không được để trống")
    @Future(message = "Ngày chiếu phải ở trong tương lai")
    LocalDate showDate;

    @Min(value = 1, message = "Hệ số giá phải >= 1")
    @Max(value = 3, message = "Hệ số giá phải <= 3")
    int coefficient;
}
