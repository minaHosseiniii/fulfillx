package com.fulfillx.order.repository;

import com.fulfillx.order.domain.model.Money;
import com.fulfillx.order.domain.model.Order;
import com.fulfillx.order.domain.model.OrderItem;
import com.fulfillx.order.domain.model.ShippingAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class OrderRepositoryIntegrationTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPersistOrderWithItems() {
        ShippingAddress shippingAddress = new ShippingAddress(
                "Mina Hosseini",
                "09120000000",
                "Tehran",
                "Tehran",
                "Example street",
                "1659000000"
        );

        Order order = new Order(1l, shippingAddress, "IRR");
        OrderItem item1 = new OrderItem(100l, "lipstick", new Money(new BigDecimal("1000"), "IRR"), 1);
        OrderItem item2 = new OrderItem(200l, "mascara", new Money(new BigDecimal("1000"), "IRR"), 1);
        order.addItem(item1);
        order.addItem(item2);
        Order savedOrder = orderRepository.save(order);
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getItems().size()).isEqualTo(2);
        assertThat(savedOrder.getTotalAmount().getAmount()).isEqualTo(new BigDecimal("2000"));
        assertThat(savedOrder.getTotalAmount().getCurrency()).isEqualTo("IRR");


    }
}
