package com.fulfillx.delivery_service.dto;

import com.fulfillx.delivery_service.domain.model.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class DeliveryResponse {

    private Long id;

    private Long orderId;

    private DeliveryStatus deliveryStatus;

    private String recipientName;

    private String phoneNumber;

    private String province;

    private String city;

    private String addressLine;

    private String postalCode;

    private String trackingNumber;

    private Instant shippedAt;

    private Instant deliveredAt;

    private String failureReason;

    private Instant createdAt;

    private Instant updatedAt;
}
