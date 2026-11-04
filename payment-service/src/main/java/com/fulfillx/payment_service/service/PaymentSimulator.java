package com.fulfillx.payment_service.service;

import com.fulfillx.payment_service.domain.model.Money;
import com.fulfillx.payment_service.dto.PaymentSimulationResult;

public interface PaymentSimulator {
    PaymentSimulationResult process(Money money);
}
