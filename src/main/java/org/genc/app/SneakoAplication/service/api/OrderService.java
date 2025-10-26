package org.genc.app.SneakoAplication.service.api;

import org.genc.app.SneakoAplication.dto.OrderDTO;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO findOrderById(Long id);
}