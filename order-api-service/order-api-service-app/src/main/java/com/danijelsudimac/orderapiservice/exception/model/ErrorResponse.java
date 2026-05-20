package com.danijelsudimac.orderapiservice.exception.model;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String code,
        List<FieldError> errors,
        Instant timestamp
) {}
