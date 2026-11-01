package com.fulfillx.inventory_service.service;

import com.fulfillx.inventory_service.events.messaging.InventoryReservationResultEvent;

public interface InventoryReservationEventService {
    void saveResult(InventoryReservationResultEvent event);
    void saveFailureResult(InventoryReservationResultEvent event);
}
