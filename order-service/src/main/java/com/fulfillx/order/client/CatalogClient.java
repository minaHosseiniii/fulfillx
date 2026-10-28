package com.fulfillx.order.client;

import com.fulfillx.order.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("/api/v1/products/{id}")
    ProductResponse getProduct(@PathVariable("id") Long productId);
}
