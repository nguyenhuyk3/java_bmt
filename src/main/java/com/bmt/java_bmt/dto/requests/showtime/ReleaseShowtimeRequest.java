package com.bmt.java_bmt.dto.requests.showtime;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReleaseShowtimeRequest {
    @NotNull(message = "ID của suất chiếu không được để trống")
    UUID id;
}
