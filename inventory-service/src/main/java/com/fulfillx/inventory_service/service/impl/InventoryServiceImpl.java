package com.fulfillx.inventory_service.service.impl;

import com.fulfillx.inventory_service.domain.model.InventoryItem;
import com.fulfillx.inventory_service.domain.model.InventoryReservation;
import com.fulfillx.inventory_service.domain.model.OutboxEvent;
import com.fulfillx.inventory_service.domain.model.ReservationStatus;
import com.fulfillx.inventory_service.dto.InventoryRequest;
import com.fulfillx.inventory_service.dto.InventoryResponse;
import com.fulfillx.inventory_service.dto.ReservationItemRequest;
import com.fulfillx.inventory_service.dto.ReserveInventoryRequest;
import com.fulfillx.inventory_service.events.messaging.InventoryReservationResultEvent;
import com.fulfillx.inventory_service.exception.InsufficientStockException;
import com.fulfillx.inventory_service.exception.InventoryAlreadyExistsException;
import com.fulfillx.inventory_service.exception.InventoryNotFoundException;
import com.fulfillx.inventory_service.mapper.InventoryMapper;
import com.fulfillx.inventory_service.repository.InventoryRepository;
import com.fulfillx.inventory_service.repository.InventoryReservationRepository;
import com.fulfillx.inventory_service.repository.OutboxEventRepository;
import com.fulfillx.inventory_service.service.InventoryReservationEventService;
import com.fulfillx.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final InventoryReservationRepository reservationRepository;
    private final InventoryReservationEventService reservationEventService;
    private final OutboxEventRepository outboxEventRepository;

    @Override
    public InventoryResponse initializeStock(InventoryRequest inventoryRequest) {
        if (inventoryRepository.existsByProductId(inventoryRequest.getProductId())) {
            throw new InventoryAlreadyExistsException("Inventory already exists for product Id: " + inventoryRequest.getProductId());
        }
        if (inventoryRepository.existsBySku(inventoryRequest.getSku())) {
            throw new InventoryAlreadyExistsException("Inventory already exists for sku: " + inventoryRequest.getSku());
        }
        InventoryItem inventoryItem = new InventoryItem(
                inventoryRequest.getProductId(),
                inventoryRequest.getSku(),
                inventoryRequest.getInitialQuantity()
        );

        InventoryItem savedItem = inventoryRepository.save(inventoryItem);
        return inventoryMapper.toInventoryResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getStock(Long productId) {
        InventoryItem inventoryItem = findInventoryItem(productId);
        return inventoryMapper.toInventoryResponse(inventoryItem);
    }

    @Override
    public InventoryResponse reserveStock(Long productId, int quantity) {
        InventoryItem inventoryItem = findInventoryItemForUpdate(productId);
        inventoryItem.reserve(quantity);
        return inventoryMapper.toInventoryResponse(inventoryItem);
    }

    @Override
    public InventoryResponse releaseStock(Long productId, int quantity) {
        InventoryItem inventoryItem = findInventoryItemForUpdate(productId);
        inventoryItem.release(quantity);
        return inventoryMapper.toInventoryResponse(inventoryItem);
    }

    @Override
    public InventoryResponse deductStock(Long productId, int quantity) {
        InventoryItem inventoryItem = findInventoryItemForUpdate(productId);
        inventoryItem.deduct(quantity);
        return inventoryMapper.toInventoryResponse(inventoryItem);
    }

    @Override
    @Transactional
    public void reserveOrder(ReserveInventoryRequest request) {

        // 1. Validate request
        if (request == null ||
                request.sagaId() == null ||
                request.orderId() == null) {

            throw new IllegalArgumentException(
                    "Invalid reservation request"
            );
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one item"
            );
        }

        // 2. Aggregate duplicate product items
        Map<Long, Integer> quantities = new HashMap<>();

        for (ReservationItemRequest item : request.items()) {

            if (item == null ||
                    item.productId() == null ||
                    item.quantity() == null ||
                    item.quantity() <= 0) {

                throw new IllegalArgumentException(
                        "Invalid reservation item"
                );
            }

            quantities.merge(
                    item.productId(),
                    item.quantity(),
                    Integer::sum
            );
        }

        // 3. Check previous reservations
        boolean alreadyReserved =
                reservationRepository.existsByOrderIdAndSagaId(
                        request.orderId(),
                        request.sagaId()
                );

        if (alreadyReserved) {

            List<InventoryReservation> reservations =
                    reservationRepository.findAllByOrderIdAndSagaId(
                            request.orderId(),
                            request.sagaId()
                    );

            validateExistingReservations(reservations, quantities);
        }

        // 4. Check previous result event
        Optional<OutboxEvent> previousResult =
                outboxEventRepository
                        .findByAggregateIdAndAggregateTypeAndSagaIdAndEventType(
                                request.orderId(),
                                "INVENTORY_RESERVATION",
                                request.sagaId(),
                                "INVENTORY_RESERVATION_RESULT"
                        );

        if (previousResult.isPresent()) {
            return;
        }

        // 5. Lock inventory records
        List<Long> productIds = quantities.keySet()
                .stream()
                .sorted()
                .toList();

        List<InventoryItem> inventoryItems =
                inventoryRepository.findAllByProductIdsForUpdate(productIds);

        if (inventoryItems.size() != productIds.size()) {
            throw new InventoryNotFoundException(
                    "Inventory not found for one or more products"
            );
        }

        Map<Long, InventoryItem> inventoryMap =
                inventoryItems.stream()
                        .collect(Collectors.toMap(
                                InventoryItem::getProductId,
                                Function.identity()
                        ));

        // 6. Validate available quantities
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {

            InventoryItem inventory = inventoryMap.get(entry.getKey());

            if (inventory.getAvailableQuantity() < entry.getValue()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + entry.getKey()
                );
            }
        }

        // 7. Reserve inventory and create reservations
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {

            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            InventoryItem inventory = inventoryMap.get(productId);

            inventory.reserve(quantity);

            InventoryReservation reservation =
                    new InventoryReservation(
                            request.orderId(),
                            request.sagaId(),
                            productId,
                            quantity
                    );

            reservationRepository.save(reservation);
        }

        // 8. Save successful result event in the same transaction
        InventoryReservationResultEvent resultEvent =
                new InventoryReservationResultEvent(
                        UUID.randomUUID(),
                        request.sagaId(),
                        request.orderId(),
                        Instant.now(),
                        true,
                        null
                );

        reservationEventService.saveResult(resultEvent);
    }

    private void validateExistingReservations(
            List<InventoryReservation> reservations,
            Map<Long, Integer> requestedQuantities
    ) {
        if (reservations.size() != requestedQuantities.size()) {
            throw new IllegalStateException(
                    "Existing reservation does not match the request"
            );
        }

        for (InventoryReservation reservation : reservations) {
            Integer requestedQuantity =
                    requestedQuantities.get(reservation.getProductId());

            if (requestedQuantity == null ||
                    !requestedQuantity.equals(reservation.getQuantity()) ||
                    reservation.getStatus() != ReservationStatus.RESERVED) {
                throw new IllegalStateException(
                        "Reservation already exists with a different state or quantity"
                );
            }
        }
    }


    private InventoryItem findInventoryItemForUpdate(Long productId) {
        return inventoryRepository.findByProductIdForUpdate(productId).orElseThrow(() ->
                new InventoryNotFoundException("Inventory not found for productId: " + productId));
    }

    private InventoryItem findInventoryItem(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product Id: " + productId));
    }
}
