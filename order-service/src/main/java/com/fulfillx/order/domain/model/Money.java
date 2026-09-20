package com.fulfillx.order.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Money {
    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(length = 3)
    private String currency;
}
