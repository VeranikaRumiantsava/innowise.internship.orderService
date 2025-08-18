package org.innowise.internship.orderservice.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.innowise.internship.orderservice.dto.order.OrderCreateDTO;
import org.innowise.internship.orderservice.dto.order.OrderFullDTO;
import org.innowise.internship.orderservice.dto.order.OrderUpdateDTO;
import org.innowise.internship.orderservice.dto.user.UserResponseDTO;
import org.innowise.internship.orderservice.entities.Order;

import org.innowise.internship.orderservice.jwt.JwtUtil;
import org.innowise.internship.orderservice.mappers.OrderMapper;
import org.innowise.internship.orderservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserClientService userClientService;
    private final JwtUtil jwtUtil;

    private final OrderMapper orderMapper;


    public OrderFullDTO createOrder(OrderCreateDTO orderCreateDTO, Long userId) {

        Order order = orderMapper.orderCreateDTOtoOrder(orderCreateDTO);
        order.setUserId(userId);

        Order savedOrder = orderRepository.save(order);

        OrderFullDTO orderFullDTO = orderMapper.orderToOrderFullDTO(savedOrder);

        orderFullDTO.setUser(
                userClientService.getUserById(userId, jwtUtil.generateAccessToken(userId))
        );

        return orderFullDTO;
    }

    @Transactional
    public OrderFullDTO updateOrder(Long orderId, OrderUpdateDTO orderUpdateDTO, Long userId) {

        Optional<Order> currentOrder = orderRepository.findById(orderId);

        if (currentOrder.isEmpty()) {
            throw new RuntimeException("Order doesn't exists");
        }

        if (!currentOrder.get().getUserId().equals(userId)) {
            throw new RuntimeException("This order isn't current user");
        }

        orderMapper.updateOrderFromOrderUpdateDTO(orderUpdateDTO, currentOrder.get());

        Order savedOrder = orderRepository.save(currentOrder.get());

        OrderFullDTO orderFullDTO = orderMapper.orderToOrderFullDTO(savedOrder);

        orderFullDTO.setUser(
                userClientService.getUserById(userId, jwtUtil.generateAccessToken(userId))
        );

        return orderFullDTO;
    }

    public OrderFullDTO getById(Long orderId, Long userId) {
        Optional<Order> currentOrder = orderRepository.findById(orderId);
        if (currentOrder.isEmpty()) {
            throw new RuntimeException("Order doesn't exists");
        }
        if (!currentOrder.get().getUserId().equals(userId)) {
            throw new RuntimeException("This order isn't current user");
        }

        OrderFullDTO orderFullDTO = orderMapper.orderToOrderFullDTO(currentOrder.get());

        orderFullDTO.setUser(userClientService.getUserById(userId, jwtUtil.generateAccessToken(userId)));

        return orderFullDTO;
    }

    public List<OrderFullDTO> getByIds(List<Long> orderIds, Long userId) {
        List<Order> listOrder = orderRepository.findByIdInAndUserId(orderIds, userId);

        UserResponseDTO userResponseDTO = userClientService.getUserById(userId, jwtUtil.generateAccessToken(userId));

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

        UserResponseDTO userResponseDTO = userClientService.getUserById(userId, jwtUtil.generateAccessToken(userId));

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
        Optional<Order> currentOrder = orderRepository.findById(orderId);
        if (currentOrder.isEmpty()) {
            throw new RuntimeException("Order doesn't exists");
        }
        if (!currentOrder.get().getUserId().equals(userId)) {
            throw new RuntimeException("This order isn't current user");
        }
        orderRepository.deleteById(orderId);
    }
}
