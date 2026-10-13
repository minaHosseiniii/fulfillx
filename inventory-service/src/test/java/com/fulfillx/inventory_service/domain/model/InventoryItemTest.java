package com.fulfillx.inventory_service.domain.model;

import com.fulfillx.inventory_service.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryItemTest {

    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        inventoryItem = new InventoryItem(
                1L,
                "IPH17P-256-BLK",
                10
        );
    }


    @Test
    void reserve_shouldDecreaseAvailableAndIncreaseReservedQuantity() {
        inventoryItem.reserve(3);

        assertEquals(7, inventoryItem.getAvailableQuantity());
        assertEquals(3, inventoryItem.getReservedQuantity());
    }

    @Test
    void reserve_shouldAllowReservingExactAvailableQuantity() {
        inventoryItem.reserve(10);

        assertEquals(0, inventoryItem.getAvailableQuantity());
        assertEquals(10, inventoryItem.getReservedQuantity());
    }

    @Test
    void reserve_shouldThrowExceptionWhenAvailableStockIsInsufficient() {
        assertThrows(
                InsufficientStockException.class,
                () -> inventoryItem.reserve(11)
        );

        // State must remain unchanged after failure
        assertEquals(10, inventoryItem.getAvailableQuantity());
        assertEquals(0, inventoryItem.getReservedQuantity());
    }

    @Test
    void reserve_shouldThrowExceptionWhenQuantityIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryItem.reserve(0)
        );

        assertEquals(10, inventoryItem.getAvailableQuantity());
        assertEquals(0, inventoryItem.getReservedQuantity());
    }

    @Test
    void reserve_shouldThrowExceptionWhenQuantityIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryItem.reserve(-2)
        );

        assertEquals(10, inventoryItem.getAvailableQuantity());
        assertEquals(0, inventoryItem.getReservedQuantity());
    }


    @Test
    void release_shouldDecreaseReservedAndIncreaseAvailableQuantity() {
        inventoryItem.reserve(5);

        inventoryItem.release(2);

        assertEquals(7, inventoryItem.getAvailableQuantity());
        assertEquals(3, inventoryItem.getReservedQuantity());
    }

    @Test
    void release_shouldThrowExceptionWhenReservedStockIsInsufficient() {
        assertThrows(
                InsufficientStockException.class,
                () -> inventoryItem.release(1)
        );

        assertEquals(10, inventoryItem.getAvailableQuantity());
        assertEquals(0, inventoryItem.getReservedQuantity());
    }

    @Test
    void release_shouldThrowExceptionWhenQuantityIsZero() {
        inventoryItem.reserve(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryItem.release(0)
        );

        assertEquals(5, inventoryItem.getAvailableQuantity());
        assertEquals(5, inventoryItem.getReservedQuantity());
    }


    @Test
    void deduct_shouldDecreaseReservedQuantityOnly() {
        inventoryItem.reserve(5);

        inventoryItem.deduct(3);

        assertEquals(5, inventoryItem.getAvailableQuantity());
        assertEquals(2, inventoryItem.getReservedQuantity());
    }

    @Test
    void deduct_shouldAllowDeductingExactReservedQuantity() {
        inventoryItem.reserve(5);

        inventoryItem.deduct(5);

        assertEquals(5, inventoryItem.getAvailableQuantity());
        assertEquals(0, inventoryItem.getReservedQuantity());
    }

    @Test
    void deduct_shouldThrowExceptionWhenReservedStockIsInsufficient() {
        inventoryItem.reserve(2);

        assertThrows(
                InsufficientStockException.class,
                () -> inventoryItem.deduct(3)
        );

        assertEquals(8, inventoryItem.getAvailableQuantity());
        assertEquals(2, inventoryItem.getReservedQuantity());
    }

    @Test
    void deduct_shouldThrowExceptionWhenQuantityIsNegative() {
        inventoryItem.reserve(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryItem.deduct(-1)
        );

        assertEquals(5, inventoryItem.getAvailableQuantity());
        assertEquals(5, inventoryItem.getReservedQuantity());
    }
}

