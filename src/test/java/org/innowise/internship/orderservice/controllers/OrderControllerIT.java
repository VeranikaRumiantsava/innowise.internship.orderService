package org.innowise.internship.orderservice.controllers;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.innowise.internship.orderservice.dto.order.OrderCreateDTO;
import org.innowise.internship.orderservice.dto.order.OrderUpdateDTO;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemRequestDTO;
import org.innowise.internship.orderservice.dto.user.UserResponseDTO;
import org.innowise.internship.orderservice.entities.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



public class OrderControllerIT extends BaseIT {

    private RequestPostProcessor mockUser() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("2", "", List.of());
        return user(userDetails);
    }

    @BeforeEach
    void setupWireMock() throws Exception {
        orderRepository.deleteAll();

        WireMock.configureFor(WIREMOCK.getHost(), WIREMOCK.getFirstMappedPort());

        UserResponseDTO mockUser = new UserResponseDTO();
        mockUser.setName("Test");
        mockUser.setSurname("User");
        mockUser.setEmail("testuser@example.com");
        mockUser.setBirthDate(LocalDate.of(1995, 12, 6));

        String mockUserJson = objectMapper.writeValueAsString(mockUser);

        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/user/2"))
                .withHeader("Authorization", WireMock.containing("Bearer"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockUserJson)
                        .withStatus(200)));
    }

    @Nested
    class CreateOrderTests {

        @Test
        void createOrderShouldReturnStatus200OkWhenValidOrder() throws Exception {
            OrderItemRequestDTO item = new OrderItemRequestDTO(1L, 2);
            OrderCreateDTO createDTO = new OrderCreateDTO();
            createDTO.setOrderItems(List.of(item));

            mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderItems[0].item.name").value("Harry's Wand"))
                    .andExpect(jsonPath("$.orderItems[0].quantity").value(2))
                    .andExpect(jsonPath("$.user.name").value("Test"));
        }

        @Test
        void createOrderShouldReturnStatus400BadRequestWhenOrderItemsEmpty() throws Exception {
            OrderCreateDTO createDTO = new OrderCreateDTO();
            createDTO.setOrderItems(List.of());

            mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createOrderShouldReturnStatus404NotFoundWhenItemDoesNotExist() throws Exception {
            OrderItemRequestDTO item = new OrderItemRequestDTO(9999L, 1);
            OrderCreateDTO createDTO = new OrderCreateDTO();
            createDTO.setOrderItems(List.of(item));

            mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void createOrderShouldReturnStatus400BadRequestWhenInvalidJsonProvided() throws Exception {
            String invalidJson = "{ invalid json }";

            mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdateOrderTests {

        @Test
        void updateOrderShouldReturnStatus200OkWhenValidUpdate() throws Exception {
            OrderItemRequestDTO item = new OrderItemRequestDTO(1L, 2);
            OrderCreateDTO createDTO = new OrderCreateDTO();
            createDTO.setOrderItems(List.of(item));

            String content = mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andReturn().getResponse().getContentAsString();
            Long orderId = objectMapper.readTree(content).get("id").asLong();

            OrderUpdateDTO updateDTO = new OrderUpdateDTO();
            updateDTO.setOrderItems(List.of(new OrderItemRequestDTO(1L, 5)));

            mockMvc.perform(patch("/api/v1/orders/{orderId}", orderId)
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderItems[0].quantity").value(5));
        }

        @Test
        void updateOrderShouldReturnStatus400BadRequestWhenInvalidStatusProvided() throws Exception {
            OrderUpdateDTO invalidDTO = new OrderUpdateDTO();
            invalidDTO.setOrderItems(List.of(new OrderItemRequestDTO(1L, -2)));
            invalidDTO.setStatus(OrderStatus.CANCELLED);

            mockMvc.perform(patch("/api/v1/orders/{orderId}", 1)
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetOrderTests {

        @Test
        void getOrderByIdShouldReturnStatus200OkWhenOrderExists() throws Exception {
            OrderItemRequestDTO item = new OrderItemRequestDTO(2L, 1);
            OrderCreateDTO createDTO = new OrderCreateDTO();
            createDTO.setOrderItems(List.of(item));

            String content = mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andReturn().getResponse().getContentAsString();
            Long orderId = objectMapper.readTree(content).get("id").asLong();

            mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                            .with(mockUser()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId));
        }

        @Test
        void getOrderByIdShouldReturnStatus404NotFoundWhenOrderDoesNotExist() throws Exception {
            mockMvc.perform(get("/api/v1/orders/{orderId}", 9999L)
                            .with(mockUser()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getOrderByIdShouldReturnStatus400BadRequestWhenIdInvalid() throws Exception {
            mockMvc.perform(get("/api/v1/orders/{orderId}", "abc")
                            .with(mockUser()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void getOrdersByIdsShouldReturnStatus200OkWhenMultipleOrdersExist() throws Exception {
            OrderItemRequestDTO item1 = new OrderItemRequestDTO(1L, 1);
            OrderItemRequestDTO item2 = new OrderItemRequestDTO(2L, 2);

            OrderCreateDTO order1 = new OrderCreateDTO();
            order1.setOrderItems(List.of(item1));
            String content1 = mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(order1)))
                    .andReturn().getResponse().getContentAsString();
            Long orderId1 = objectMapper.readTree(content1).get("id").asLong();

            OrderCreateDTO order2 = new OrderCreateDTO();
            order2.setOrderItems(List.of(item2));
            String content2 = mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(order2)))
                    .andReturn().getResponse().getContentAsString();
            Long orderId2 = objectMapper.readTree(content2).get("id").asLong();

            mockMvc.perform(get("/api/v1/orders/ids")
                            .param("ids", orderId1.toString(), orderId2.toString())
                            .with(mockUser()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void getOrdersByStatusShouldReturnStatus200OkWhenOrdersExistWithGivenStatus() throws Exception {
            OrderItemRequestDTO item = new OrderItemRequestDTO(1L, 1);
            OrderCreateDTO createDTO = new OrderCreateDTO();
            createDTO.setOrderItems(List.of(item));
            String content = mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andReturn().getResponse().getContentAsString();
            Long orderId = objectMapper.readTree(content).get("id").asLong();

            mockMvc.perform(get("/api/v1/orders/status/NEW")
                            .with(mockUser()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(orderId));
        }
    }

    @Nested
    class DeleteOrderTests {

        @Test
        void deleteOrderShouldReturnStatus204NoContentWhenOrderDeleted() throws Exception {
            OrderItemRequestDTO item = new OrderItemRequestDTO(3L, 1);
            OrderCreateDTO createDTO = new OrderCreateDTO();
            createDTO.setOrderItems(List.of(item));

            String content = mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andReturn().getResponse().getContentAsString();
            Long orderId = objectMapper.readTree(content).get("id").asLong();

            mockMvc.perform(delete("/api/v1/orders/{orderId}", orderId)
                            .with(mockUser()))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteOrderShouldReturnStatus401UnauthorizedWhenAccessingOtherUsersOrder() throws Exception {
            OrderItemRequestDTO item = new OrderItemRequestDTO(1L, 2);
            OrderCreateDTO createDTO = new OrderCreateDTO();
            createDTO.setOrderItems(List.of(item));

            mockMvc.perform(post("/api/v1/orders")
                            .with(mockUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/api/v1/orders/{orderId}", 1)
                            .with(user("3")))
                    .andExpect(status().isUnauthorized());
        }
    }

}
