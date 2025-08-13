package org.innowise.internship.orderservice.mappers;

import org.innowise.internship.orderservice.dto.orderitem.OrderItemRequestDTO;
import org.innowise.internship.orderservice.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { OrderMapper.class, ItemMapper.class })
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem orderItemRequestDTOtoOrderItem(OrderItemRequestDTO orderItemRequestDTO);
}
