package com.bmt.java_bmt.dto.others;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoPayload {
    String partnerCode;
    String accessKey;
    String requestId;
    int amount;
    String orderId;
    String orderInfo;
    String partnerName;
    String storeId;
    String orderGroupId;
    String lang;
    boolean autoCapture;
    String redirectUrl;
    String ipnUrl;
    String extraData;
    String requestType;
    String signature;
}
