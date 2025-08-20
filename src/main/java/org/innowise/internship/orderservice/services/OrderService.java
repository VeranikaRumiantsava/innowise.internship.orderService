package org.innowise.internship.orderservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.innowise.internship.orderservice.dto.order.OrderCreateDTO;
import org.innowise.internship.orderservice.dto.order.OrderFullDTO;
import org.innowise.internship.orderservice.dto.order.OrderUpdateDTO;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemRequestDTO;
import org.innowise.internship.orderservice.dto.user.UserResponseDTO;
import org.innowise.internship.orderservice.entities.Item;
import org.innowise.internship.orderservice.entities.Order;

import org.innowise.internship.orderservice.entities.OrderItem;
import org.innowise.internship.orderservice.exceptions.ItemNotFoundException;
import org.innowise.internship.orderservice.exceptions.OrderNotFoundException;
import org.innowise.internship.orderservice.jwt.JwtUtil;
import org.innowise.internship.orderservice.mappers.OrderMapper;
import org.innowise.internship.orderservice.repositories.ItemRepository;
import org.innowise.internship.orderservice.repositories.OrderRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserClientService userClientService;
    private final JwtUtil jwtUtil;

    private final OrderMapper orderMapper;

    private final ItemRepository itemRepository;

    public OrderFullDTO createOrder(OrderCreateDTO orderCreateDTO, Long userId) {

        Order order = orderMapper.orderCreateDTOtoOrder(orderCreateDTO);
        order.setUserId(userId);

        for (OrderItemRequestDTO orderItemRequestDTO : orderCreateDTO.getOrderItems()) {
            Item item = itemRepository.findById(orderItemRequestDTO.getItemId())
                    .orElseThrow(() -> new ItemNotFoundException("Item id: " + orderItemRequestDTO.getItemId() + " does not exists"));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(orderItemRequestDTO.getQuantity());

            order.getOrderItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        OrderFullDTO orderFullDTO = orderMapper.orderToOrderFullDTO(savedOrder);
        orderFullDTO.setUser(userClientService.getUserById(userId));

        return orderFullDTO;
    }

    @Transactional
    public OrderFullDTO updateOrder(Long orderId, OrderUpdateDTO orderUpdateDTO, Long userId) {

        Order currentOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order id: " + orderId + " does not exist"));

        if (!currentOrder.getUserId().equals(userId)) {
            throw new AccessDeniedException("This order isn't current user");
        }

        orderMapper.updateOrderFromOrderUpdateDTO(orderUpdateDTO, currentOrder);

        if (orderUpdateDTO.getOrderItems() != null) {
            for (OrderItemRequestDTO dto : orderUpdateDTO.getOrderItems()) {
                Item item = itemRepository.findById(dto.getItemId())
                        .orElseThrow(() -> new ItemNotFoundException("Item id: " + dto.getItemId() + " does not exists"));
                currentOrder.addOrUpdateOrderItem(dto, item);
            }
        }

        Order savedOrder = orderRepository.save(currentOrder);
        OrderFullDTO orderFullDTO = orderMapper.orderToOrderFullDTO(savedOrder);

        orderFullDTO.setUser(userClientService.getUserById(userId));

        return orderFullDTO;
    }

    public OrderFullDTO getById(Long orderId, Long userId) {
        Order currentOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order id: " + orderId + " does not exist"));

        if (!currentOrder.getUserId().equals(userId)) {
            throw new AccessDeniedException("This order isn't current user");
        }

        OrderFullDTO orderFullDTO = orderMapper.orderToOrderFullDTO(currentOrder);

        orderFullDTO.setUser(userClientService.getUserById(userId));

        return orderFullDTO;
    }

    public List<OrderFullDTO> getByIds(List<Long> orderIds, Long userId) {
        List<Order> listOrder = orderRepository.findByIdInAndUserId(orderIds, userId);

        UserResponseDTO userResponseDTO = userClientService.getUserById(userId);

        return listOrder.stream()
                .map(orderFullDTO -> {
                    OrderFullDTO dto = orderMapper.orderToOrderFullDTO(orderFullDTO);
                    dto.setUser(userResponseDTO);
                    return dto;
                })
                .toList();
    }

    public List<OrderFullDTO> getByStatus(String status, Long userId) {
        List<Order> listOrder = orderRepository.findByStatusAndUserId(status, userId);

        UserResponseDTO userResponseDTO = userClientService.getUserById(userId);

        return listOrder.stream()
                .map(orderFullDTO -> {
                    OrderFullDTO dto = orderMapper.orderToOrderFullDTO(orderFullDTO);
                    dto.setUser(userResponseDTO);
                    return dto;
                })
                .toList();
    }

    @Transactional
    public void deleteById(Long orderId, Long userId) {
        Order currentOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order id: " + orderId + " does not exist"));

        if (!currentOrder.getUserId().equals(userId)) {
            throw new AccessDeniedException("This order isn't current user");
        }

        orderRepository.deleteById(orderId);
    }
}
