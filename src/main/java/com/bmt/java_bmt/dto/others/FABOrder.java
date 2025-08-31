package com.bmt.java_bmt.dto.others;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FABOrder {
    @NotNull(message = "Không được để trống id của đồ ăn")
    @JsonProperty("fABId")
    UUID fABId;

    @Positive(message = "Số lượng phải lớn hơn 0")
    int quantity;
}
