package org.innowise.internship.orderservice.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemRequestDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Setter
@Getter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrUpdateOrderItem(OrderItemRequestDTO orderItemRequestDTO, Item item) {
        OrderItem existing = orderItems.stream()
                .filter(orderItem -> orderItem.getItem().getId().equals(orderItemRequestDTO.getItemId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(orderItemRequestDTO.getQuantity());
        } else {
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setItem(item);
            newOrderItem.setQuantity(orderItemRequestDTO.getQuantity());
            newOrderItem.setOrder(this);
            orderItems.add(newOrderItem);
        }
    }
}
