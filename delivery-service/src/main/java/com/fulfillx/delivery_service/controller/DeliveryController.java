package com.fulfillx.delivery_service.controller;


import com.fulfillx.delivery_service.dto.CreateDeliveryRequest;
import com.fulfillx.delivery_service.dto.DeliveryResponse;
import com.fulfillx.delivery_service.service.DeliveryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@Validated
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(
            @Valid @RequestBody CreateDeliveryRequest request) {

        DeliveryResponse response =
                deliveryService.createDelivery(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                deliveryService.getDeliveryById(id)
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getDeliveryByOrderId(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                deliveryService.getDeliveryByOrderId(orderId)
        );
    }

    @PatchMapping("/{id}/preparing")
    public ResponseEntity<DeliveryResponse> startPreparing(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                deliveryService.startPreparing(id)
        );
    }

    @PatchMapping("/{id}/ready-for-pickup")
    public ResponseEntity<DeliveryResponse> markReadyForPickup(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                deliveryService.markReadyForPickup(id)
        );
    }

    @PatchMapping("/{id}/in-transit")
    public ResponseEntity<DeliveryResponse> markInTransit(
            @PathVariable Long id,
            @RequestParam
            @NotBlank
            @Size(max = 100)
            String trackingNumber) {

        return ResponseEntity.ok(
                deliveryService.markInTransit(id, trackingNumber)
        );
    }

    @PatchMapping("/{id}/delivered")
    public ResponseEntity<DeliveryResponse> markDelivered(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                deliveryService.markDelivered(id)
        );
    }

    @PatchMapping("/{id}/failed")
    public ResponseEntity<DeliveryResponse> markFailed(
            @PathVariable Long id,
            @RequestParam
            @NotBlank
            @Size(max = 500)
            String reason) {

        return ResponseEntity.ok(
                deliveryService.markFailed(id, reason)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<DeliveryResponse> cancelDelivery(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                deliveryService.cancelDelivery(id)
        );
    }
}
