package org.innowise.internship.orderservice.dto.order;

import org.innowise.internship.orderservice.dto.orderitem.OrderItemFullDTO;

import java.time.LocalDateTime;
import java.util.List;

public class OrderFullDTO {
    private Long id;

    private Long userId;

    private String status;

    private LocalDateTime creationDate;

    private List<OrderItemFullDTO> orderItems;
}
