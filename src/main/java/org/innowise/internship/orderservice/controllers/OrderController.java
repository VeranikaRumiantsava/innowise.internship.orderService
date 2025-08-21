package org.innowise.internship.orderservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.innowise.internship.orderservice.dto.order.OrderFullDTO;
import org.innowise.internship.orderservice.dto.order.OrderUpdateDTO;
import org.innowise.internship.orderservice.dto.order.OrderCreateDTO;
import org.innowise.internship.orderservice.entities.OrderStatus;
import org.innowise.internship.orderservice.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    private Long getIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String userId = userDetails.getUsername();
            id = Long.parseLong(userId);
        }
        return id;
    }

    @PostMapping
    public ResponseEntity<OrderFullDTO> createOrder(@RequestBody @Valid OrderCreateDTO orderCreateDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderCreateDTO, getIdFromAuthentication()));
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderFullDTO> updateOrder(@PathVariable Long orderId,
                                                    @RequestBody @Valid OrderUpdateDTO orderUpdateDTO) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderUpdateDTO, getIdFromAuthentication()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderFullDTO> getById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getById(orderId, getIdFromAuthentication()));
    }

    @GetMapping("/ids")
    public ResponseEntity<List<OrderFullDTO>> getByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(orderService.getByIds(ids, getIdFromAuthentication()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderFullDTO>> getByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getByStatus(status, getIdFromAuthentication()));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long orderId) {
        orderService.deleteById(orderId, getIdFromAuthentication());
        return ResponseEntity.noContent().build();
    }
}
