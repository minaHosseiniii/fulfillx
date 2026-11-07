package com.fulfillx.inventory_service.service;

import com.fulfillx.inventory_service.events.messaging.InventoryReleaseResultEvent;
import com.fulfillx.inventory_service.events.messaging.InventoryReservationResultEvent;

import java.util.UUID;

public interface InventoryReservationEventService {
    void saveReservationResult(InventoryReservationResultEvent event);
    void saveFailureResult(InventoryReservationResultEvent event);
    void ensureSuccessResultExists(UUID sagaId, Long orderId);
    void saveReleaseResult(InventoryReleaseResultEvent event);
}
