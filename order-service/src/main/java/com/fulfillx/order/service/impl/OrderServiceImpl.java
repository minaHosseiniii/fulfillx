package com.fulfillx.order.service.impl;

import com.fulfillx.order.domain.dto.CreateOrderItemRequest;
import com.fulfillx.order.domain.dto.CreateOrderRequest;
import com.fulfillx.order.domain.dto.OrderResponse;
import com.fulfillx.order.domain.model.Money;
import com.fulfillx.order.domain.model.Order;
import com.fulfillx.order.domain.model.OrderItem;
import com.fulfillx.order.mapper.OrderMapper;
import com.fulfillx.order.repository.OrderRepository;
import com.fulfillx.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(CreateOrderRequest orderRequest) {
        Order order = new Order(
                orderRequest.customerId(),
                orderMapper.toShippingAddress(orderRequest.shippingAddress()),
                orderRequest.currency()
        );

        for (CreateOrderItemRequest itemRequest: orderRequest.items()) {
            OrderItem item = new OrderItem(
                    itemRequest.productId(),
                    itemRequest.productName(),
                    new Money(
                            itemRequest.unitPrice(),
                            orderRequest.currency()
                    ),
                    itemRequest.quantity()
            );
            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }
}
