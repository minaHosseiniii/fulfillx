package com.fulfillx.order.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class OrderItemTest {

    @Test
    void shouldCalculateSubTotalWhenCreated() {
        OrderItem item = new OrderItem(
                100l,
                "Lipstick",
                new Money(new BigDecimal("1000"), "IRR"),
                3
        );

        assertThat(item.getSubTotal()).isEqualByComparingTo("3000");
    }

    @Test
    void shouldCalculateSubTotalWhenQuantityChanges() {
        OrderItem item = new OrderItem(
                100l,
                "Lipstick",
                new Money(new BigDecimal("1000"), "IRR"),
                3
        );

        item.changeQuantity(5);

        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getSubTotal()).isEqualByComparingTo("5000");
    }

    @Test
    void shouldRejectInvalidQuantity(){
        assertThatThrownBy(() ->
                new OrderItem(
                        100l,
                        "Lipstick",
                        new Money(new BigDecimal("1000"), "IRR"),
                        0
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantity must be greater than 0");
    }
}
