package com.fulfillx.order.domain.model;

import com.fulfillx.order.domain.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class OrderTest {
    private ShippingAddress createShippingAddress(){
        return new ShippingAddress(
                "Mina Hosseini",
                "09120000000",
                "Tehran",
                "Tehran",
                "Example Street",
                "1659234567"
        );
    }

    @Test
    void shouldCalculateSubTotalWhenItemsAdded() {
        Order order = new Order(
                1l,
                createShippingAddress(),
                "IRR"
        );
        order.addItem(new OrderItem(
                100l,
                "Lipstick",
                new Money(new BigDecimal("1000"), "IRR"),
                1
        ));
        order.addItem(new OrderItem(
                200l,
                "masckara",
                new Money(new BigDecimal("2000"), "IRR"),
                1
        ));

        assertThat(order.getItems().size()).isEqualTo(2);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("3000"));
    }


    @Test
    void shouldCalculateSubTotalWhenItemsRemoved() {
        Order order = new Order(
                1l,
                createShippingAddress(),
                "IRR"
        );
        OrderItem item = new OrderItem(
                100l,
                "Lipstick",
                new Money(new BigDecimal("1000"), "IRR"),
                1
        );

        OrderItem item2 = new OrderItem(
                200l,
                "masckara",
                new Money(new BigDecimal("2000"), "IRR"),
                1
        );

        order.addItem(item);
        order.addItem(item2);
        order.removeItem(item);

        assertThat(order.getItems()).containsExactly(item2);

        assertThat(order.getTotalAmount().getAmount())
                .isEqualByComparingTo("2000");

    }

    @Test
    void shouldRejectItemWithDifferentCurrency() {
        Order order = new Order(
                1L,
                createShippingAddress(),
                "IRR"
        );

        OrderItem item = new OrderItem(
                100L,
                "Lipstick",
                new Money(new BigDecimal("10"), "USD"),
                1
        );

        assertThatThrownBy(() -> order.addItem(item))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order item currency must match order currency");

        assertThat(order.getItems()).isEmpty();
    }

    @Test
    void shouldMoveOrderThroughSuccessfulLifecycle() {
        Order order = new Order(
                1L,
                createShippingAddress(),
                "IRR"
        );

        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.PENDING);

        order.startInventoryReservation();
        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.INVENTORY_RESERVATION_PENDING);

        order.markInventoryReserved();
        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.INVENTORY_RESERVED);

        order.startPayment();
        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.PAYMENT_PENDING);

        order.confirm();
        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.CONFIRMED);

        order.ship();
        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.SHIPPED);

        order.deliver();
        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void shouldRejectInvalidStatusTransition() {
        Order order = new Order(
                1L,
                createShippingAddress(),
                "IRR"
        );

        assertThatThrownBy(order::confirm)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldCancelOrder() {
        Order order = new Order(
                1L,
                createShippingAddress(),
                "IRR"
        );

        order.cancel();

        assertThat(order.getOrderStatus())
                .isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldRejectCancellationAfterDelivery() {
        Order order = new Order(
                1L,
                createShippingAddress(),
                "IRR"
        );

        order.startInventoryReservation();
        order.markInventoryReserved();
        order.startPayment();
        order.confirm();
        order.ship();
        order.deliver();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Delivered order cannot be cancelled");
    }
}
