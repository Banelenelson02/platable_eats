package com.plateable.dto.request;

import jakarta.validation.constraints.NotBlank;
//this is just a comment mize it
public record CreateOrderRequest(
        @NotBlank(message = "tableId is required") String tableId,
        @NotBlank(message = "waiterId is required") String waiterId
) {}
