package org.innowise.internship.orderservice.dto.item;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemFullDTO {
    private String name;

    private BigDecimal price;
}
