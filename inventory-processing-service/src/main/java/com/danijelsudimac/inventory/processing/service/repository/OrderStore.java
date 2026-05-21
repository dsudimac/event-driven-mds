package com.danijelsudimac.inventory.processing.service.repository;

import com.danijelsudimac.inventory.processing.service.model.Order;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class OrderStore {

    private static final String IDEMPOTENCY_EXCEPTION_MESSAGE = "Order with ID %s has already been processed";
    private static final String OUT_OF_STOCK_MESSAGE = "No item with ID {} in stock or insufficient quantity";
    private static final String ERROR_PROCESSING_MESSAGE = "Error processing order {}: {}";
    private final Map<String, Long> stock = new ConcurrentHashMap<>();
    private final Set<String> processedOrders = ConcurrentHashMap.newKeySet();

    @PostConstruct
    void init() {
        stock.put("item-1", 10L);
        stock.put("item-2", 5L);
    }

    public synchronized boolean reserve(Order order) {
        if (processedOrders.contains(order.orderId())) { //idempotency check
            log.error(ERROR_PROCESSING_MESSAGE, order.orderId(), IDEMPOTENCY_EXCEPTION_MESSAGE);
            return false;
        }

        var available = stock.get(order.itemId());
        if (available == null || available < order.quantity()) {
            log.error(OUT_OF_STOCK_MESSAGE, order.itemId());
            return false;
        }

        stock.put(order.itemId(), available - order.quantity());
        processedOrders.add(order.orderId());
        return true;
    }
}
