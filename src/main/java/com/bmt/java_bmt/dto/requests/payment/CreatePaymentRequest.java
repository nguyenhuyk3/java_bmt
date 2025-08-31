package com.bmt.java_bmt.dto.requests.payment;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePaymentRequest {
    int amount;
    String errorMessage;
    String transactionId;
    UUID orderId;
}
