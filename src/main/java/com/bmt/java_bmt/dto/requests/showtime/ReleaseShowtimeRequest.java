package com.bmt.java_bmt.dto.requests.showtime;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReleaseShowtimeRequest {
    @NotNull(message = "ID của suất chiếu không được để trống")
    UUID id;
}
