package com.danijelsudimac.order.api.service.app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderRequest(
        @NotBlank String orderId,
        @NotBlank String itemId,
        @NotNull @Positive Integer quantity
) {}
