package org.innowise.internship.orderservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innowise.internship.orderservice.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public abstract class BaseIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected OrderRepository orderRepository;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("test_user")
                    .withUsername("user")
                    .withPassword("password");

    @Container
    public static final WireMockContainer WIREMOCK =
            new WireMockContainer("wiremock/wiremock:2.35.0")
                    .withExposedPorts(8080);

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL::getPassword);

        registry.add("user.service.url", () ->
                "http://" + WIREMOCK.getHost() + ":" + WIREMOCK.getFirstMappedPort()
        );
    }
}
