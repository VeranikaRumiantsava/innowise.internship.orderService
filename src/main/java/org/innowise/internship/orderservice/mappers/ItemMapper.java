package org.innowise.internship.orderservice.mappers;


import org.innowise.internship.orderservice.dto.item.ItemFullDTO;
import org.innowise.internship.orderservice.entities.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class, ItemMapper.class })
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    Item itemFullDTOToItem(ItemFullDTO itemFullDTO);
}
