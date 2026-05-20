package com.danijelsudimac.order.api.service.app.exception.model;

public record FieldError(
        String field,
        String message
) {}
