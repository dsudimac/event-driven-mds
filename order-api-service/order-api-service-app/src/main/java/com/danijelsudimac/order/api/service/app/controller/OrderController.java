package com.danijelsudimac.order.api.service.app.controller;

import com.danijelsudimac.order.api.service.app.mapper.OrderMapper;
import com.danijelsudimac.order.api.service.app.model.CreateOrderRequest;
import com.danijelsudimac.order.api.service.app.service.OrderPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    public static final String TRACE_ID_KEY = "traceId";

    private final OrderPublisher orderPublisher;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String traceId = UUID.randomUUID().toString();
        try {
            MDC.put(TRACE_ID_KEY, traceId); //should be replaced by micrometer observability in production code
            orderPublisher.publish(orderMapper.toCreateOrderEvent(request));
            return ResponseEntity.accepted().build();
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
