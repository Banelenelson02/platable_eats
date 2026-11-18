package com.plateable.dto.response;

public record CheckoutResponse(String orderId, String status, double total, int itemCount) {}
