package com.danijelsudimac.orderapiservice.mapper;

import com.danijelsudimac.orderapiservice.model.CreateOrderEvent;
import com.danijelsudimac.orderapiservice.model.CreateOrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "receivedAt", expression = "java(java.time.Instant.now())")
    CreateOrderEvent toCreateOrderEvent(CreateOrderRequest request);
}
