package org.genc.app.SneakoAplication.service.api;

import org.genc.app.SneakoAplication.dto.OrderItemDTO;
import java.util.List;

public interface OrderItemService {
    OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO);
    OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO);
    void deleteOrderItem(Long id);
    OrderItemDTO findOrderItemById(Long id);
    List<OrderItemDTO> findItemsByOrderId(Long orderId);
}