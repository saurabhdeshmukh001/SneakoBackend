package org.genc.app.SneakoAplication.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private String shippingAddress;
    private BigDecimal totalPrice;
    private String orderStatus;
    private LocalDateTime orderDate;
    private List<OrderItemDTO> orderItems;
}