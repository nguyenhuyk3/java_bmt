package com.bmt.java_bmt.dto.requests.order;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.bmt.java_bmt.dto.others.FABOrder;
import com.bmt.java_bmt.dto.others.SeatOrder;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequest {
    @NotNull(message = "Mã xuất chiếu không được để trống")
    UUID showtimeId;

    @Valid
    @NotNull(message = "Ghế phải có thông tin")
    List<SeatOrder> seats;

    @Valid
    List<FABOrder> fABs;
}
