package com.bmt.java_bmt.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bmt.java_bmt.dto.others.MomoPayload;
import com.bmt.java_bmt.dto.responses.payment.momo.CreateMomoPaymentResponse;

@FeignClient(name = "momo", url = "${momo.end-point}")
public interface IMomoClient {
    @PostMapping("/create")
    CreateMomoPaymentResponse createMomoQR(@RequestBody MomoPayload request);
}
