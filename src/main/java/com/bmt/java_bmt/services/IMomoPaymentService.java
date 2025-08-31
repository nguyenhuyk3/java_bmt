package com.bmt.java_bmt.services;

import com.bmt.java_bmt.dto.requests.payment.momo.CreateMomoPaymentRequest;
import com.bmt.java_bmt.dto.responses.payment.momo.CreateMomoPaymentResponse;

public interface IMomoPaymentService {
    CreateMomoPaymentResponse createMomoQR(CreateMomoPaymentRequest request);
}
