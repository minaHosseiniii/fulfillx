package com.fulfillx.payment_service.events;

import com.fulfillx.payment_service.events.saga.ProcessPaymentCommand;
import com.fulfillx.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessingConsumer {
    private final PaymentService paymentService;

    @KafkaListener(
            groupId = "payment-service",
            topics = "payment-processing-command"
    )
    public void consume(ProcessPaymentCommand command){
        log.info("Received payment command: eventId = {}, sagaId = {}, amount = {}, currencey = {}",
                command.eventId(),
                command.sagaId(),
                command.amount(),
                command.currency());

        paymentService.processPayment(command);
    }

}
