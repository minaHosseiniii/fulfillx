package com.fulfillx.catalog_service.service;

import com.fulfillx.catalog_service.dto.ProductRequest;
import com.fulfillx.catalog_service.dto.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);

    ProductResponse findProductById(Long productId);

    ProductResponse updateProduct(Long productId, ProductRequest request);

    void activateProduct(Long productId);

    void deactivateProduct(Long productId);
}
