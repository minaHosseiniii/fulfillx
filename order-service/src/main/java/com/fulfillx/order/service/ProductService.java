package com.fulfillx.order.service;

import com.fulfillx.order.client.dto.ProductResponse;

public interface ProductService {
    ProductResponse getProduct(Long productId);
}
