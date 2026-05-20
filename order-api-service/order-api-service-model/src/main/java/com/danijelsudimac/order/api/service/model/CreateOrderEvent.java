package com.danijelsudimac.order.api.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record CreateOrderEvent (
        @NotBlank String orderId,
        @NotBlank String itemId,
        @NotNull @Positive Integer quantity,
        @NotNull Instant receivedAt
) {}
