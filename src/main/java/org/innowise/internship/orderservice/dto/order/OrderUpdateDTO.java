package org.innowise.internship.orderservice.dto.order;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemRequestDTO;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class OrderUpdateDTO {
    @Size(max = 50)
    private String status;

    private List<OrderItemRequestDTO> orderItems = new ArrayList<>();
}
