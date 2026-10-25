package com.fulfillx.order.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fulfillx.order.domain.dto.CreateOrderItemRequest;
import com.fulfillx.order.domain.dto.CreateOrderRequest;
import com.fulfillx.order.domain.dto.OrderResponse;
import com.fulfillx.order.domain.model.Money;
import com.fulfillx.order.domain.model.Order;
import com.fulfillx.order.domain.model.OrderItem;
import com.fulfillx.order.domain.model.OutboxEvent;
import com.fulfillx.order.events.OrderCreatedEvent;
import com.fulfillx.order.mapper.OrderMapper;
import com.fulfillx.order.repository.OrderRepository;
import com.fulfillx.order.repository.OutboxEventRepository;
import com.fulfillx.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

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

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                UUID.randomUUID(),
                "ORDER_CREATED",
                Instant.now(),
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                savedOrder.getTotalAmount().getAmount(),
                savedOrder.getTotalAmount().getCurrency()
        );


        try {
            String payload = objectMapper.writeValueAsString(orderCreatedEvent);
            OutboxEvent outboxEvent = new OutboxEvent(
                    orderCreatedEvent.eventId(),
                    "ORDER",
                    savedOrder.getId(),
                    orderCreatedEvent.eventType(),
                    payload
            );
            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize orderCreatedEvent", e);
        }

        return orderMapper.toOrderResponse(savedOrder);
    }
}
