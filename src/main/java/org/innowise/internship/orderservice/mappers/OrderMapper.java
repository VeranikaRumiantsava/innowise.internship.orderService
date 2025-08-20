package org.innowise.internship.orderservice.mappers;


import org.innowise.internship.orderservice.dto.order.OrderCreateDTO;
import org.innowise.internship.orderservice.dto.order.OrderFullDTO;
import org.innowise.internship.orderservice.dto.order.OrderUpdateDTO;
import org.innowise.internship.orderservice.entities.Order;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        imports = {java.time.LocalDateTime.class})
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", expression = "java(\"NEW\")")
    @Mapping(target = "creationDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "orderItems", expression = "java(new java.util.ArrayList<>())")
    Order orderCreateDTOtoOrder(OrderCreateDTO orderCreateDTO);

    @Mapping(target = "user", ignore = true)
    OrderFullDTO orderToOrderFullDTO(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderFromOrderUpdateDTO(OrderUpdateDTO userUpdateDTO, @MappingTarget Order order);
}
