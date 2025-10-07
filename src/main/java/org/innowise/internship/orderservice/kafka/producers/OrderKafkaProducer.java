package org.innowise.internship.orderservice.kafka.producers;

import lombok.RequiredArgsConstructor;
import org.innowise.internship.orderservice.dto.kafka.OrderDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    public void sendCreateOrder(OrderDTO orderDTO) {
        kafkaTemplate.send("CREATE_ORDER_TOPIC", orderDTO);
    }
}
