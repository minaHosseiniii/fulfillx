package com.fulfillx.payment_service.domain.model;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    PENDING,

    PROCESSING,

    SUCCEEDED,

    FAILED,

    CANCELLED
}
