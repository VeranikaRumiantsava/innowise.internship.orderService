package org.innowise.internship.orderservice.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Setter
@Getter
public class OrderShortDTO {
    private Long id;

    private Long userId;

    private String status;

    private LocalDateTime creationDate;

    private BigDecimal totalSum;
}

