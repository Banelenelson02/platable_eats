package com.plateable.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOrderRequest(
        @NotBlank(message = "tableId is required") String tableId,
        @NotBlank(message = "waiterId is required") String waiterId
) {}
