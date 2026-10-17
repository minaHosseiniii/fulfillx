package com.fulfillx.delivery_service.service;

import com.fulfillx.delivery_service.dto.CreateDeliveryRequest;
import com.fulfillx.delivery_service.dto.DeliveryResponse;

public interface DeliveryService {
    DeliveryResponse createDelivery(CreateDeliveryRequest request);

    DeliveryResponse getDeliveryById(Long id);

    DeliveryResponse getDeliveryByOrderId(Long orderId);

    DeliveryResponse startPreparing(Long id);

    DeliveryResponse markReadyForPickup(Long id);

    DeliveryResponse markInTransit(Long id, String trackingNumber);

    DeliveryResponse markDelivered(Long id);

    DeliveryResponse markFailed(Long id, String reason);

    DeliveryResponse cancelDelivery(Long id);
}
