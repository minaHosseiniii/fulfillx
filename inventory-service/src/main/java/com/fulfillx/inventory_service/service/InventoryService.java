package com.fulfillx.inventory_service.service;

import com.fulfillx.inventory_service.dto.InventoryRequest;
import com.fulfillx.inventory_service.dto.InventoryResponse;

public interface InventoryService {
    InventoryResponse initializeStock(InventoryRequest inventoryRequest);
    InventoryResponse getStock(Long productId);
    InventoryResponse reserveStock(Long productId, int quantity);
    InventoryResponse releaseStock(Long productId, int quantity);
    InventoryResponse deductStock(Long productId, int quantity);
}
