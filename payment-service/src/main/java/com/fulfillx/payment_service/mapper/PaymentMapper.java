package com.fulfillx.payment_service.mapper;


import com.fulfillx.payment_service.domain.model.Payment;
import com.fulfillx.payment_service.dto.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "amount", source = "money.amount")
    @Mapping(target = "currency", source = "money.currency")
    PaymentResponse toPaymentResponse(Payment payment);
}
