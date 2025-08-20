package org.innowise.internship.orderservice.dto.order;

import lombok.Getter;
import lombok.Setter;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemFullDTO;
import org.innowise.internship.orderservice.dto.user.UserResponseDTO;
import org.innowise.internship.orderservice.entities.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderFullDTO {
    private UserResponseDTO user;

    private Long id;

    private OrderStatus status;

    private LocalDateTime creationDate;

    private List<OrderItemFullDTO> orderItems;


}
