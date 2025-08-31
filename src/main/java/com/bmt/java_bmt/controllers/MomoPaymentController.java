package com.bmt.java_bmt.controllers;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.payment.CreatePaymentRequest;
import com.bmt.java_bmt.dto.requests.payment.momo.CreateMomoPaymentRequest;
import com.bmt.java_bmt.dto.responses.payment.momo.CreateMomoPaymentResponse;
import com.bmt.java_bmt.entities.enums.PaymentMethod;
import com.bmt.java_bmt.entities.enums.PaymentStatus;
import com.bmt.java_bmt.exceptions.AppException;
import com.bmt.java_bmt.exceptions.ErrorCode;
import com.bmt.java_bmt.services.IMomoPaymentService;
import com.bmt.java_bmt.services.IPaymentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/payment/momo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MomoPaymentController {
    IMomoPaymentService momoPaymentService;
    IPaymentService paymentService;

    @PostMapping("/create-qr")
    public APIResponse<CreateMomoPaymentResponse> createQR(@RequestBody @Valid CreateMomoPaymentRequest request) {
        var result = momoPaymentService.createMomoQR(request);

        return APIResponse.<CreateMomoPaymentResponse>builder().result(result).build();
    }

    @GetMapping("/ipn-handler")
    public APIResponse<String> ipnHandler(@RequestParam Map<String, String> request) {
        try {
            int resultCode = Integer.parseInt(request.get("resultCode"));
            UUID orderId = UUID.fromString(request.get("orderId"));
            CreatePaymentRequest paymentRequest = CreatePaymentRequest.builder()
                    .amount(Integer.parseInt(request.get("amount")))
                    .errorMessage(request.getOrDefault("errorMessage", ""))
                    .transactionId(request.get("transId"))
                    .orderId(orderId)
                    .build();
            PaymentStatus status = (resultCode == 0) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
            // ✅ Gọi service xử lý
            paymentService.handlePayment(paymentRequest, status, PaymentMethod.MOMO);

            return APIResponse.<String>builder()
                    .result("Thông báo sẽ được gửi về email của bạn")
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.MOMO_IPN_INVALID);
        }
    }
}
