package org.innowise.internship.orderservice.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrUpdateOrderItem(OrderItemRequestDTO dto, Item item) {
        OrderItem existing = orderItems.stream()
                .filter(oi -> oi.getItem().getId().equals(dto.getItemId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(dto.getQuantity());
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setItem(item);
            newItem.setQuantity(dto.getQuantity());
            newItem.setOrder(this);
            orderItems.add(newItem);
        }
    }
}
