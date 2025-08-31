package com.bmt.java_bmt.dto.requests.payment.momo;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMomoPaymentRequest {
    @NotNull(message = "Mã đơn hàng không được để trống")
    UUID orderId;

    @Min(value = 1000, message = "Số tiền thanh toán phải lớn hơn hoặc bằng 1000 VNĐ")
    int amount;
}
