package org.innowise.internship.orderservice.services;

import org.innowise.internship.orderservice.dto.order.OrderCreateDTO;
import org.innowise.internship.orderservice.dto.order.OrderFullDTO;
import org.innowise.internship.orderservice.dto.order.OrderUpdateDTO;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemRequestDTO;
import org.innowise.internship.orderservice.dto.user.UserResponseDTO;
import org.innowise.internship.orderservice.entities.Item;
import org.innowise.internship.orderservice.entities.Order;
import org.innowise.internship.orderservice.exceptions.ItemNotFoundException;
import org.innowise.internship.orderservice.exceptions.OrderNotFoundException;
import org.innowise.internship.orderservice.mappers.OrderMapper;
import org.innowise.internship.orderservice.repositories.ItemRepository;
import org.innowise.internship.orderservice.repositories.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserClientService userClientService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderShouldReturnOrderFullDTO() {
        Long userId = 1L;
        OrderCreateDTO createDTO = new OrderCreateDTO();
        OrderItemRequestDTO itemDTO = new OrderItemRequestDTO();
        itemDTO.setItemId(10L);
        itemDTO.setQuantity(2);
        createDTO.setOrderItems(List.of(itemDTO));

        Order order = new Order();
        Item item = new Item();
        Order savedOrder = new Order();
        OrderFullDTO orderFullDTO = new OrderFullDTO();
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        Mockito.when(orderMapper.orderCreateDTOtoOrder(createDTO)).thenReturn(order);
        Mockito.when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        Mockito.when(orderRepository.save(order)).thenReturn(savedOrder);
        Mockito.when(orderMapper.orderToOrderFullDTO(savedOrder)).thenReturn(orderFullDTO);
        Mockito.when(userClientService.getUserById(userId)).thenReturn(userResponseDTO);

        OrderFullDTO result = orderService.createOrder(createDTO, userId);

        Assertions.assertEquals(orderFullDTO, result);
        Assertions.assertEquals(userResponseDTO, result.getUser());
    }

    @Test
    void createOrderShouldThrowWhenItemNotFound() {
        Long userId = 1L;
        OrderCreateDTO createDTO = new OrderCreateDTO();
        OrderItemRequestDTO itemDTO = new OrderItemRequestDTO();
        itemDTO.setItemId(10L);
        createDTO.setOrderItems(List.of(itemDTO));

        Mockito.when(orderMapper.orderCreateDTOtoOrder(createDTO)).thenReturn(new Order());
        Mockito.when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ItemNotFoundException.class, () -> orderService.createOrder(createDTO, userId));
    }

    @Test
    void updateOrderShouldUpdateAndReturnOrder() {
        Long orderId = 1L;
        Long userId = 2L;
        OrderUpdateDTO updateDTO = new OrderUpdateDTO();
        updateDTO.setOrderItems(List.of());

        Order currentOrder = new Order();
        currentOrder.setUserId(userId);
        Order savedOrder = new Order();
        OrderFullDTO orderFullDTO = new OrderFullDTO();
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(currentOrder));
        Mockito.when(orderRepository.save(currentOrder)).thenReturn(savedOrder);
        Mockito.when(orderMapper.orderToOrderFullDTO(savedOrder)).thenReturn(orderFullDTO);
        Mockito.when(userClientService.getUserById(userId)).thenReturn(userResponseDTO);

        OrderFullDTO result = orderService.updateOrder(orderId, updateDTO, userId);

        Assertions.assertEquals(orderFullDTO, result);
        Assertions.assertEquals(userResponseDTO, result.getUser());
    }

    @Test
    void updateOrderShouldThrowWhenOrderNotFound() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(1L, new OrderUpdateDTO(), 1L));
    }

    @Test
    void updateOrderShouldThrowWhenAccessDenied() {
        Order order = new Order();
        order.setUserId(2L);
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Assertions.assertThrows(AccessDeniedException.class,
                () -> orderService.updateOrder(1L, new OrderUpdateDTO(), 1L));
    }

    @Test
    void getByIdShouldReturnOrderFullDTO() {
        Long orderId = 1L;
        Long userId = 2L;
        Order order = new Order();
        order.setUserId(userId);
        OrderFullDTO dto = new OrderFullDTO();
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderMapper.orderToOrderFullDTO(order)).thenReturn(dto);
        Mockito.when(userClientService.getUserById(userId)).thenReturn(userResponseDTO);

        OrderFullDTO result = orderService.getById(orderId, userId);

        Assertions.assertEquals(dto, result);
        Assertions.assertEquals(userResponseDTO, result.getUser());
    }

    @Test
    void getByIdShouldThrowAccessDenied() {
        Order order = new Order();
        order.setUserId(2L);
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Assertions.assertThrows(AccessDeniedException.class, () -> orderService.getById(1L, 1L));
    }

    @Test
    void deleteByIdShouldDeleteOrder() {
        Long orderId = 1L;
        Long userId = 2L;
        Order order = new Order();
        order.setUserId(userId);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.deleteById(orderId, userId);

        Mockito.verify(orderRepository).deleteById(orderId);
    }

    @Test
    void deleteByIdShouldThrowAccessDenied() {
        Order order = new Order();
        order.setUserId(2L);
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Assertions.assertThrows(AccessDeniedException.class, () -> orderService.deleteById(1L, 1L));
    }

    @Test
    void deleteByIdShouldThrowOrderNotFound() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class, () -> orderService.deleteById(1L, 1L));
    }
}
