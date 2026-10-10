package com.fulfillx.inventory_service.controller;

import com.fulfillx.inventory_service.dto.InventoryRequest;
import com.fulfillx.inventory_service.dto.InventoryResponse;
import com.fulfillx.inventory_service.dto.StockOperationRequest;
import com.fulfillx.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> initializeStock(
            @RequestBody @Valid InventoryRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.initializeStock(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getStock(
            @PathVariable @Positive Long productId
    ) {
        return ResponseEntity.ok(
                inventoryService.getStock(productId)
        );
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable @Positive Long productId,
            @RequestBody @Valid StockOperationRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.reserveStock(
                        productId,
                        request.getQuantity()
                )
        );
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable @Positive Long productId,
            @RequestBody @Valid StockOperationRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.releaseStock(
                        productId,
                        request.getQuantity()
                )
        );
    }

    @PostMapping("/{productId}/deduct")
    public ResponseEntity<InventoryResponse> deductStock(
            @PathVariable @Positive Long productId,
            @RequestBody @Valid StockOperationRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.deductStock(
                        productId,
                        request.getQuantity()
                )
        );
    }
}