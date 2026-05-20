package com.danijelsudimac.orderapiservice.exception.model;

public record FieldError(
        String field,
        String message
) {}
