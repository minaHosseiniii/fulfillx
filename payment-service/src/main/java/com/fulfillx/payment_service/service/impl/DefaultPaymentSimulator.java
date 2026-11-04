package com.fulfillx.payment_service.service.impl;

import com.fulfillx.payment_service.domain.model.Money;
import com.fulfillx.payment_service.dto.PaymentSimulationResult;
import com.fulfillx.payment_service.service.PaymentSimulator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultPaymentSimulator implements PaymentSimulator {
    @Override
    public PaymentSimulationResult process(Money money) {
        return new PaymentSimulationResult(
                true,
                null,
                "SIMULATOR",
                "SIM-" + UUID.randomUUID()
        );
    }
}
