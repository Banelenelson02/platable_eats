package com.plateable.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CheckoutRequest(
        @NotBlank @Size(min = 2, max = 100) String customerName,
        @NotBlank @Pattern(regexp = "^[+0-9 ()-]{7,20}$") String contactNumber,
        @NotBlank @Size(min = 5, max = 400) String deliveryAddress,
        @Size(max = 500) String specialInstructions,
        @NotEmpty @Size(max = 50) List<@Valid CheckoutItem> items
) {
    public record CheckoutItem(
            @NotBlank String menuItemId,
            @Min(1) @Max(50) int quantity
    ) {}
}
