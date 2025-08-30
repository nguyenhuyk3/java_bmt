package com.bmt.java_bmt.controllers;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmt.java_bmt.dto.APIResponse;
import com.bmt.java_bmt.dto.requests.order.CreateOrderRequest;
import com.bmt.java_bmt.services.IOrderService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    IOrderService orderService;

    @PostMapping()
    @PreAuthorize("hasRole('CUSTOMER')")
    public APIResponse<String> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        var result = orderService.createOrder(request);

        return APIResponse.<String>builder().result(result).build();
    }
}
