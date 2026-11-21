package com.fulfillx.order.service.impl;

import com.fulfillx.order.client.CatalogClient;
import com.fulfillx.order.client.dto.ProductResponse;
import com.fulfillx.order.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CatalogClient catalogClient;

    @Override
    @Cacheable(cacheNames = "products", key = "#productId")
    public ProductResponse getProduct(Long productId) {
        return catalogClient.getProduct(productId);
    }
}
