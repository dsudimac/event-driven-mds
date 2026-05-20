package com.danijelsudimac.inventory_processing_service.model;

/*
 * This record represents an order entity that will be stored in the database.
 */
public record Order (String orderId, String itemId, Integer quantity) {}
