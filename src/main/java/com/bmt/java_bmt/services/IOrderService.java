package com.bmt.java_bmt.services;

import com.bmt.java_bmt.dto.requests.order.CreateOrderRequest;

public interface IOrderService {
    String createOrder(CreateOrderRequest request);
}
