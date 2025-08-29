package com.bmt.java_bmt.dto.others;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FABOrder {
    @NotNull(message = "Không được để trống id của đồ ăn")
    UUID fABId;

    @Positive(message = "Số lượng phải lớn hơn 0")
    int quantity;
}
