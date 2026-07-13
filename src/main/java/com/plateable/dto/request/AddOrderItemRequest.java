package com.plateable.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AddOrderItemRequest(
        @NotBlank(message = "menuItemId is required") String menuItemId,
        @Positive(message = "quantity must be greater than 0") int quantity,
        String instructions
) {}
