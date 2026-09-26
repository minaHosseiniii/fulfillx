package com.fulfillx.order.domain.dto;

import java.math.BigDecimal;

public record MoneyResponse(
        BigDecimal amount,
        String currency
) {
}
