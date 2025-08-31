package com.bmt.java_bmt.services;

import com.bmt.java_bmt.dto.requests.payment.CreatePaymentRequest;
import com.bmt.java_bmt.entities.enums.PaymentMethod;
import com.bmt.java_bmt.entities.enums.PaymentStatus;

public interface IPaymentService {
    void handlePayment(CreatePaymentRequest request, PaymentStatus paymentStatus, PaymentMethod paymentMethod);
}
