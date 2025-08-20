package org.innowise.internship.orderservice.dto.order;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.innowise.internship.orderservice.dto.orderitem.OrderItemRequestDTO;


import java.util.List;

@Setter
@Getter
public class OrderCreateDTO {
   @NotNull(message = "Order items are required")
   @Size(min = 1, message = "At least one order item is required")
   private List<OrderItemRequestDTO> orderItems;
}
