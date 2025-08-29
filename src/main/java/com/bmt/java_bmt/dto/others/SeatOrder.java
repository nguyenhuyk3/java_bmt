package com.bmt.java_bmt.dto.others;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatOrder {
    @NotNull(message = "Không được để trống id của ghế")
    UUID seatId;
}
