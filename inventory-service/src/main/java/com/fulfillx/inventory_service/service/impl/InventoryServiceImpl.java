package com.fulfillx.inventory_service.service.impl;

import com.fulfillx.inventory_service.domain.model.InventoryItem;
import com.fulfillx.inventory_service.dto.InventoryRequest;
import com.fulfillx.inventory_service.dto.InventoryResponse;
import com.fulfillx.inventory_service.exception.InventoryAlreadyExistsException;
import com.fulfillx.inventory_service.exception.InventoryNotFoundException;
import com.fulfillx.inventory_service.mapper.InventoryMapper;
import com.fulfillx.inventory_service.repository.InventoryRepository;
import com.fulfillx.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

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
        InventoryItem inventoryItem = findInventoryItem(productId);
        inventoryItem.reserve(quantity);
        return inventoryMapper.toInventoryResponse(inventoryItem);
    }

    @Override
    public InventoryResponse releaseStock(Long productId, int quantity) {
        InventoryItem inventoryItem = findInventoryItem(productId);
        inventoryItem.release(quantity);
        return inventoryMapper.toInventoryResponse(inventoryItem);
    }

    @Override
    public InventoryResponse deductStock(Long productId, int quantity) {
        InventoryItem inventoryItem = findInventoryItem(productId);
        inventoryItem.deduct(quantity);
        return inventoryMapper.toInventoryResponse(inventoryItem);
    }

    private InventoryItem findInventoryItem(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product Id: " + productId));
    }
}
