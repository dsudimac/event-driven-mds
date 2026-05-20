package com.danijelsudimac.order.api.service.app.mapper;

import com.danijelsudimac.order.api.service.app.model.CreateOrderRequest;
import com.danijelsudimac.order.api.service.model.CreateOrderEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "receivedAt", expression = "java(java.time.Instant.now())")
    CreateOrderEvent toCreateOrderEvent(CreateOrderRequest request);
}
