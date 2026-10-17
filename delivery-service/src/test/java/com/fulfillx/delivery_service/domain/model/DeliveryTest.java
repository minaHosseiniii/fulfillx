package com.fulfillx.delivery_service.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {

    private DeliveryAddress validAddress() {
        return new DeliveryAddress(
                "Mina Hosseini",
                "09120000000",
                "Tehran",
                "Tehran",
                "Valiasr Street, No. 10",
                "1234567890"
        );
    }

    @Test
    void createsDeliveryWithPendingStatus() {
        Delivery delivery = Delivery.create(1L, validAddress());

        assertEquals(1L, delivery.getOrderId());
        assertEquals(DeliveryStatus.PENDING, delivery.getDeliveryStatus());
        assertNotNull(delivery.getDeliveryAddress());
        assertNull(delivery.getTrackingNumber());
    }

    @Test
    void rejectsNonPositiveOrderId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Delivery.create(0L, validAddress())
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> Delivery.create(-1L, validAddress())
        );
    }

    @Test
    void rejectsNullDeliveryAddress() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Delivery.create(1L, null)
        );
    }

    @Test
    void followsValidDeliveryLifecycle() {
        Delivery delivery = Delivery.create(1L, validAddress());

        delivery.startPreparing();
        assertEquals(DeliveryStatus.PREPARING, delivery.getDeliveryStatus());

        delivery.markReadyForPickup();
        assertEquals(
                DeliveryStatus.READY_FOR_PICKUP,
                delivery.getDeliveryStatus()
        );

        delivery.markInTransit("TRACK-123");
        assertEquals(DeliveryStatus.IN_TRANSIT, delivery.getDeliveryStatus());
        assertEquals("TRACK-123", delivery.getTrackingNumber());
        assertNotNull(delivery.getShippedAt());

        delivery.markDelivered();
        assertEquals(DeliveryStatus.DELIVERED, delivery.getDeliveryStatus());
        assertNotNull(delivery.getDeliveredAt());
    }

    @Test
    void rejectsInvalidStatusTransition() {
        Delivery delivery = Delivery.create(1L, validAddress());

        assertThrows(
                IllegalStateException.class,
                delivery::markDelivered
        );

        assertThrows(
                IllegalStateException.class,
                () -> delivery.markInTransit("TRACK-123")
        );

        assertEquals(DeliveryStatus.PENDING, delivery.getDeliveryStatus());
    }

    @Test
    void rejectsBlankTrackingNumber() {
        Delivery delivery = Delivery.create(1L, validAddress());

        delivery.startPreparing();
        delivery.markReadyForPickup();

        assertThrows(
                IllegalArgumentException.class,
                () -> delivery.markInTransit(" ")
        );

        assertEquals(
                DeliveryStatus.READY_FOR_PICKUP,
                delivery.getDeliveryStatus()
        );
        assertNull(delivery.getShippedAt());
    }

    @Test
    void recordsFailureReasonWhenDeliveryFails() {
        Delivery delivery = Delivery.create(1L, validAddress());

        delivery.startPreparing();
        delivery.markFailed("Package damaged");

        assertEquals(DeliveryStatus.FAILED, delivery.getDeliveryStatus());
        assertEquals("Package damaged", delivery.getFailureReason());
    }

    @Test
    void rejectsFailureWithoutReason() {
        Delivery delivery = Delivery.create(1L, validAddress());
        delivery.startPreparing();

        assertThrows(
                IllegalArgumentException.class,
                () -> delivery.markFailed(" ")
        );

        assertEquals(DeliveryStatus.PREPARING, delivery.getDeliveryStatus());
    }

    @Test
    void allowsCancellationOnlyBeforePickupPreparationCompletes() {
        Delivery delivery = Delivery.create(1L, validAddress());

        delivery.cancel();

        assertEquals(DeliveryStatus.CANCELLED, delivery.getDeliveryStatus());

        Delivery secondDelivery = Delivery.create(2L, validAddress());
        secondDelivery.startPreparing();
        secondDelivery.markReadyForPickup();

        assertThrows(
                IllegalStateException.class,
                secondDelivery::cancel
        );
    }
}

