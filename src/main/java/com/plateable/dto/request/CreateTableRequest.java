package com.plateable.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateTableRequest(
        @NotBlank(message = "tableId is required") String tableId,
        @Positive(message = "capacity must be greater than 0") int capacity
) {}
