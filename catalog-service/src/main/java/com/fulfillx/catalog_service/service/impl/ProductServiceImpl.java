package com.fulfillx.catalog_service.service.impl;

import com.fulfillx.catalog_service.domain.model.Product;
import com.fulfillx.catalog_service.dto.ProductRequest;
import com.fulfillx.catalog_service.dto.ProductResponse;
import com.fulfillx.catalog_service.exception.ProductAlreadyExistException;
import com.fulfillx.catalog_service.exception.ProductNotFoundException;
import com.fulfillx.catalog_service.mapper.ProductMapper;
import com.fulfillx.catalog_service.repository.ProductRepository;
import com.fulfillx.catalog_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {

        if (productRepository.existsBySku(request.sku())) {
            throw new ProductAlreadyExistException(
                    "Product with SKU " + request.sku() + " already exists"
            );
        }

        Product product = new Product(
                request.name(),
                request.description(),
                request.sku(),
                request.price(),
                request.currency()
        );

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findProductById(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found")
                );

        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse updateProduct(
            Long productId,
            ProductRequest request
    ) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found")
                );

        if (!product.getSku().equals(request.sku())
                && productRepository.existsBySku(request.sku())) {

            throw new ProductAlreadyExistException(
                    "Product with SKU " + request.sku() + " already exists"
            );
        }

        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.currency()
        );

        return productMapper.toProductResponse(product);
    }

    @Override
    public void activateProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found")
                );

        product.activate();
    }

    @Override
    public void deactivateProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found")
                );

        product.deactivate();
    }
}