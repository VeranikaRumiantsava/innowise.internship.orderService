package org.innowise.internship.orderservice.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemRequestDTO;
import org.innowise.internship.orderservice.entities.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class OrderUpdateDTO {

    private OrderStatus status;

    @Valid
    private List<OrderItemRequestDTO> orderItems;
}
