package com.plateable.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

public record UpdateMenuItemRequest(
        @PositiveOrZero(message = "price cannot be negative") Double price,
        Boolean available
) {}
