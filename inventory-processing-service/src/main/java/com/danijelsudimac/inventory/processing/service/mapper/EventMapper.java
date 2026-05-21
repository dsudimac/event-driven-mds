package com.danijelsudimac.inventory.processing.service.mapper;

import com.danijelsudimac.inventory.processing.service.model.Order;
import com.danijelsudimac.order.api.service.model.CreateOrderEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Order toOrder(CreateOrderEvent event);
}
