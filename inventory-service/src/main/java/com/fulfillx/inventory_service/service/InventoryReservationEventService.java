package com.fulfillx.inventory_service.service;

import com.fulfillx.inventory_service.events.messaging.InventoryReservationResultEvent;

import java.util.UUID;

public interface InventoryReservationEventService {
    void saveResult(InventoryReservationResultEvent event);
    void saveFailureResult(InventoryReservationResultEvent event);
    void ensureSuccessResultExists(UUID sagaId, Long orderId);
}
