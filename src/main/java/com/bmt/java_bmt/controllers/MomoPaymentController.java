package com.bmt.java_bmt.controllers;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.payment.momo.CreateMomoPaymentRequest;
import com.bmt.java_bmt.dto.responses.payment.momo.CreateMomoPaymentResponse;
import com.bmt.java_bmt.helpers.constants.Momo;
import com.bmt.java_bmt.services.IMomoPaymentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/payment/momo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MomoPaymentController {
    IMomoPaymentService momoPaymentService;

    @PostMapping("/create-qr")
    public APIResponse<CreateMomoPaymentResponse> createQR(@RequestBody @Valid CreateMomoPaymentRequest request) {
        var result = momoPaymentService.createMomoQR(request);

        return APIResponse.<CreateMomoPaymentResponse>builder().result(result).build();
    }

    @GetMapping("/ipn-handler")
    public String ipnHandler(@RequestBody Map<String, String> request) {
        Integer resultCode = Integer.valueOf(request.get(Momo.RESULT_CODE));

        return "";
    }
}
