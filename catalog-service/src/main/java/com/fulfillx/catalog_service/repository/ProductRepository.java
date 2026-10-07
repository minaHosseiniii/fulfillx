package com.fulfillx.catalog_service.repository;

import com.fulfillx.catalog_service.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsBySku(String sku);
}
