package com.bmt.java_bmt.dto.responses.payment.momo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMomoPaymentResponse {
    String partnerCode;
    String orderId;
    String requestId;
    int amount;
    long responseTime;
    String message;
    int resultCode;
    String payUrl;
    String deeplink;
    String qrCodeUrl;
}
