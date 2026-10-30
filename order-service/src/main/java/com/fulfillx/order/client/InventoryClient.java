package com.fulfillx.order.client;

import com.fulfillx.order.client.dto.InventoryResponse;
import com.fulfillx.order.client.dto.StockOperationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "inventory-service",
        path = "/api/v1/inventory"
)
public interface InventoryClient {

    @PostMapping("/{productId}/reserve")
    InventoryResponse reserveStock(
            @PathVariable("productId") Long productId,
            @RequestBody StockOperationRequest request
    );

    @PostMapping("/{productId}/release")
    InventoryResponse releaseStock(
            @PathVariable("productId") Long productId,
            @RequestBody StockOperationRequest request
    );

    @PostMapping("/{productId}/deduct")
    InventoryResponse deductStock(
            @PathVariable("productId") Long productId,
            @RequestBody StockOperationRequest request
    );
}