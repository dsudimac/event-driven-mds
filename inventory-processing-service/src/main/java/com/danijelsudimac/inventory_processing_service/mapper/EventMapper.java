package com.danijelsudimac.inventory_processing_service.mapper;

import com.danijelsudimac.inventory_processing_service.model.Order;
import com.danijelsudimac.orderapiservice.model.CreateOrderEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    Order toOrder(CreateOrderEvent event);
}
