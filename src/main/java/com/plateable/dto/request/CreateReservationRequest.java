package com.plateable.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record CreateReservationRequest(
        @NotBlank(message = "customerName is required") String customerName,
        @NotBlank(message = "tableId is required") String tableId,
        @Positive(message = "partySize must be greater than 0") int partySize,
        @Future(message = "time must be in the future") LocalDateTime time
) {}
