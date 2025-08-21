package org.innowise.internship.orderservice.dto.orderitem;

import lombok.Getter;
import lombok.Setter;
import org.innowise.internship.orderservice.dto.item.ItemFullDTO;

@Getter
@Setter
public class OrderItemFullDTO {
    private ItemFullDTO item;

    private Integer quantity;
}
