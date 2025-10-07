package org.innowise.internship.orderservice.kafka.consumers;

import lombok.RequiredArgsConstructor;
import org.innowise.internship.orderservice.dto.kafka.PaymentDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    @KafkaListener(topics = "CREATE_PAYMENT_TOPIC", groupId = "order-group")
    public void handleCreatePayment(PaymentDTO paymentDTO) {
        System.out.println("Received CREATE_PAYMENT event: " + paymentDTO);
        // Тут можно обновить статус заказа в БД
    }
}