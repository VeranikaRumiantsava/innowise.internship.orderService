package org.innowise.internship.orderservice.dto.order;

import lombok.Getter;
import lombok.Setter;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemFullDTO;
import org.innowise.internship.orderservice.dto.user.UserResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderFullDTO {
    private Long id;

    private Long userId;

    private String status;

    private LocalDateTime creationDate;

    private List<OrderItemFullDTO> orderItems;

    private UserResponseDTO user;
}
