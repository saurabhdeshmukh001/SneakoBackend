package org.genc.app.SneakoAplication.service.api.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.genc.app.SneakoAplication.domain.entity.Order;
import org.genc.app.SneakoAplication.domain.entity.OrderItem;
import org.genc.app.SneakoAplication.dto.OrderDTO;
import org.genc.app.SneakoAplication.dto.OrderItemDTO;
import org.genc.app.SneakoAplication.repo.OrderItemRepository;
import org.genc.app.SneakoAplication.repo.OrderRepository;
import org.genc.app.SneakoAplication.service.api.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {

        // 1️⃣ Save Order to generate ID
        Order order = Order.builder()
                .userId(orderDTO.getUserId())
                .shippingAddress(orderDTO.getShippingAddress())
                .orderStatus("PENDING")
                .orderDate(LocalDateTime.now())
                .build();
        Order savedOrder = orderRepository.save(order);

        // 2️⃣ Save all OrderItems
        List<OrderItem> orderItems = orderDTO.getOrderItems().stream()
                .map(dto -> OrderItem.builder()
                        .order(savedOrder) // use savedOrder
                        .productId(dto.getProductId())
                        .quantity(dto.getQuantity())
                        .unitPrice(dto.getUnitPrice())
                        .totalPrice(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                        .size(dto.getSize())
                        .build())
                .collect(Collectors.toList());

        // 3️⃣ Recalculate order total
        BigDecimal orderTotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(orderTotal);
        orderRepository.save(order);

        // 4️⃣ Reload items to get generated IDs and timestamps
        List<OrderItem> savedItems = orderItemRepository.findByOrderOrderId(order.getOrderId());

        // 5️⃣ Construct DTO
        List<OrderItemDTO> itemDTOs = savedItems.stream()
                .map(item -> OrderItemDTO.builder()
                        .orderItemId(item.getOrderItemId())
                        .orderId(order.getOrderId()) // ✅ fixed
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .size(item.getSize())

                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .shippingAddress(order.getShippingAddress())
                .orderStatus(order.getOrderStatus())
                .totalPrice(order.getTotalPrice()) // ✅ fixed
                .orderDate(order.getOrderDate())
                .orderItems(itemDTOs)
                .build();
    }
    @Override
    public OrderDTO findOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Ensure orderItems are fetched (to avoid lazy loading issues)
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .orderItemId(item.getOrderItemId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .size(item.getSize())
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .shippingAddress(order.getShippingAddress())
                .orderStatus(order.getOrderStatus())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate())
                .orderItems(itemDTOs) // ✅ add this
                .build();
    }
}