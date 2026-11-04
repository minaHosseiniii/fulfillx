package com.fulfillx.order.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fulfillx.order.client.CatalogClient;
import com.fulfillx.order.client.CustomerClient;
import com.fulfillx.order.client.dto.CustomerResponse;
import com.fulfillx.order.client.dto.ProductResponse;
import com.fulfillx.order.domain.dto.CreateOrderItemRequest;
import com.fulfillx.order.domain.dto.CreateOrderRequest;
import com.fulfillx.order.domain.dto.OrderResponse;
import com.fulfillx.order.domain.model.Money;
import com.fulfillx.order.domain.model.Order;
import com.fulfillx.order.domain.model.OrderItem;
import com.fulfillx.order.domain.model.OutboxEvent;
import com.fulfillx.order.domain.model.enums.OrderStatus;
import com.fulfillx.order.events.OrderCreatedEvent;
import com.fulfillx.order.events.messaging.InventoryReservationResultEvent;
import com.fulfillx.order.events.saga.inventory.InventoryReservationItem;
import com.fulfillx.order.events.saga.inventory.ReserveInventoryCommand;
import com.fulfillx.order.events.saga.payment.ProcessPaymentCommand;
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
    private final CatalogClient catalogClient;
    private final CustomerClient customerClient;

    @Override
    public OrderResponse createOrder(CreateOrderRequest orderRequest) {
        CustomerResponse customer = customerClient.getCustomer(orderRequest.customerId());
        if (!"ACTIVE".equals(customer.status())) {
            throw new IllegalArgumentException("Can not create order for inactive customer: " + customer.status());
        }

        Order order = new Order(
                orderRequest.customerId(),
                orderMapper.toShippingAddress(orderRequest.shippingAddress()),
                orderRequest.currency()
        );

        for (CreateOrderItemRequest itemRequest: orderRequest.items()) {
            ProductResponse product = catalogClient.getProduct(itemRequest.productId());
            if (!"ACTIVE".equals(product.status())) {
                throw new IllegalArgumentException("Can not create order for inactive product: " + itemRequest.productId());
            }
            if (!orderRequest.currency().equalsIgnoreCase(product.currency())) {
                throw new IllegalArgumentException("Product currency does not match to order currency: " + product.id());
            }
            OrderItem item = new OrderItem(
                    product.id(),
                    product.name(),
                    product.sku(),
                    new Money(
                            product.price(),
                            product.currency()
                    ),
                    itemRequest.quantity()
            );
            order.addItem(item);
        }

        UUID sagaId = UUID.randomUUID();

        order.startInventoryReservation(sagaId);

        Order savedOrder = orderRepository.save(order);

        ReserveInventoryCommand command = new ReserveInventoryCommand(
                UUID.randomUUID(),
                sagaId,
                savedOrder.getId(),
                Instant.now(),
                savedOrder.getItems().stream().map(orderItem ->
                        new InventoryReservationItem(orderItem.getProductId(), orderItem.getQuantity())
                ).toList()
        );
        try {
            String payload = objectMapper.writeValueAsString(command);
            OutboxEvent outboxEvent = new OutboxEvent(
                    command.eventId(),
                    "ORDER",
                    command.orderId(),
                    "RESERVE_INVENTORY",
                    payload
            );
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize inventory command ", e);
        }

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

    @Override
    public void handleInventoryReservationResult(InventoryReservationResultEvent event) {
        Order order = orderRepository.findById(event.orderId()).orElseThrow(() ->
                new IllegalArgumentException("Order not found: " + event.orderId()));

        if (!event.sagaId().equals(order.getSagaId())) {
            throw new IllegalStateException("Saga id mismatch for order: " + event.orderId());
        }

        if (order.getOrderStatus() != OrderStatus.INVENTORY_RESERVATION_PENDING) {
            return;
        }

        if (!event.successful()) {
            order.markInventoryReservationFailed();
            return;
        }
        order.markInventoryReserved();
        order.startPayment();
        Money totalAmount = order.calculateTotalAmount();
        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                order.getId(),
                totalAmount.getAmount(),
                totalAmount.getCurrency(),
                Instant.now()
        );
        try {
            String payload = objectMapper.writeValueAsString(processPaymentCommand);
            OutboxEvent outboxEvent = new OutboxEvent(
                    processPaymentCommand.eventId(),
                    "ORDER",
                    order.getId(),
                    "PROCESS_PAYMENT",
                    payload
            );

            outboxEventRepository.save(outboxEvent);
        }catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize processPaymentCommand", e);
        }
    }

}
