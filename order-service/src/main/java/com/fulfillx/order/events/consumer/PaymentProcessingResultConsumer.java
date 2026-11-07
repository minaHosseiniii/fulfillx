package com.fulfillx.order.events.consumer;

import com.fulfillx.order.events.saga.payment.PaymentProcessingResultEvent;
import com.fulfillx.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessingResultConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            groupId = "order-service",
            topics = "payment-processing-results"
    )
    public void consume(String message) throws Exception {

        PaymentProcessingResultEvent event =
                objectMapper.readValue(message, PaymentProcessingResultEvent.class);

        log.info("Received payment processing result: {}", event);

        orderService.handlePaymentProcessingResult(event);
    }
}
