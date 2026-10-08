package com.fulfillx.catalog_service.service.impl;

import com.fulfillx.catalog_service.domain.model.Product;
import com.fulfillx.catalog_service.domain.model.ProductStatus;
import com.fulfillx.catalog_service.dto.ProductRequest;
import com.fulfillx.catalog_service.dto.ProductResponse;
import com.fulfillx.catalog_service.exception.ProductAlreadyExistException;
import com.fulfillx.catalog_service.exception.ProductNotFoundException;
import com.fulfillx.catalog_service.mapper.ProductMapper;
import com.fulfillx.catalog_service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;
    

    @Test
    void shouldCreateProductSuccessfully() {

        ProductRequest request = new ProductRequest();
        request.setName("iPhone 17 Pro");
        request.setDescription("Apple smartphone");
        request.setSku("IPH17P-256-BLK");
        request.setPrice(new BigDecimal("1299.99"));
        request.setCurrency("USD");

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getSku(),
                request.getPrice(),
                request.getCurrency()
        );

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName(request.getName());
        response.setDescription(request.getDescription());
        response.setSku(request.getSku());
        response.setPrice(request.getPrice());
        response.setCurrency(request.getCurrency());
        response.setStatus(ProductStatus.ACTIVE);
        response.setCreatedBy("anonymous");
        response.setCreatedAt(LocalDateTime.now());

        when(productRepository.existsBySku(request.getSku()))
                .thenReturn(false);

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        when(productMapper.toProductResponse(product))
                .thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals(request.getSku(), result.getSku());
        assertEquals(request.getName(), result.getName());
        assertEquals(ProductStatus.ACTIVE, result.getStatus());

        verify(productRepository).existsBySku(request.getSku());
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toProductResponse(product);
    }


    @Test
    void shouldThrowExceptionWhenCreatingProductWithExistingSku() {

        ProductRequest request = new ProductRequest();
        request.setName("iPhone 17 Pro");
        request.setDescription("Apple smartphone");
        request.setSku("IPH17P-256-BLK");
        request.setPrice(new BigDecimal("1299.99"));
        request.setCurrency("USD");

        when(productRepository.existsBySku(request.getSku()))
                .thenReturn(true);

        ProductAlreadyExistException exception = assertThrows(
                ProductAlreadyExistException.class,
                () -> productService.createProduct(request)
        );

        assertEquals(
                "Product with SKU " + request.getSku() + " already exists",
                exception.getMessage()
        );

        verify(productRepository).existsBySku(request.getSku());
        verify(productRepository, never()).save(any(Product.class));
        verifyNoInteractions(productMapper);
    }

    @Test
    void shouldFindProductByIdSuccessfully() {

        Long productId = 1L;

        Product product = new Product(
                "iPhone 17 Pro",
                "Apple smartphone",
                "IPH17P-256-BLK",
                new BigDecimal("1299.99"),
                "USD"
        );

        ProductResponse response = new ProductResponse();
        response.setId(productId);
        response.setName("iPhone 17 Pro");
        response.setDescription("Apple smartphone");
        response.setSku("IPH17P-256-BLK");
        response.setPrice(new BigDecimal("1299.99"));
        response.setCurrency("USD");
        response.setStatus(ProductStatus.ACTIVE);
        response.setCreatedBy("anonymous");
        response.setCreatedAt(LocalDateTime.now());

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(productMapper.toProductResponse(product))
                .thenReturn(response);

        ProductResponse result = productService.findProductById(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("IPH17P-256-BLK", result.getSku());
        assertEquals("iPhone 17 Pro", result.getName());

        verify(productRepository).findById(productId);
        verify(productMapper).toProductResponse(product);
    }


    @Test
    void shouldThrowExceptionWhenProductNotFoundById() {

        Long productId = 999L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.findProductById(productId)
        );

        assertEquals(
                "Product not found",
                exception.getMessage()
        );

        verify(productRepository).findById(productId);
        verifyNoInteractions(productMapper);
    }


    @Test
    void shouldUpdateProductSuccessfully() {

        Long productId = 1L;

        Product product = new Product(
                "iPhone 17 Pro",
                "Apple smartphone",
                "IPH17P-256-BLK",
                new BigDecimal("1299.99"),
                "USD"
        );

        ProductRequest request = new ProductRequest();
        request.setName("iPhone 17 Pro Updated");
        request.setDescription("Updated Apple smartphone");
        request.setSku("IPH17P-256-BLK");
        request.setPrice(new BigDecimal("1399.99"));
        request.setCurrency("USD");

        ProductResponse response = new ProductResponse();
        response.setId(productId);
        response.setName(request.getName());
        response.setDescription(request.getDescription());
        response.setSku(request.getSku());
        response.setPrice(request.getPrice());
        response.setCurrency(request.getCurrency());
        response.setStatus(ProductStatus.ACTIVE);
        response.setCreatedBy("anonymous");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedBy("anonymous");
        response.setUpdatedAt(LocalDateTime.now());

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(productMapper.toProductResponse(product))
                .thenReturn(response);

        ProductResponse result = productService.updateProduct(
                productId,
                request
        );

        assertNotNull(result);
        assertEquals(request.getName(), result.getName());
        assertEquals(request.getPrice(), result.getPrice());
        assertEquals(request.getSku(), result.getSku());

        verify(productRepository).findById(productId);
        verify(productMapper).toProductResponse(product);
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {

        Long productId = 999L;

        ProductRequest request = new ProductRequest();
        request.setName("iPhone 17 Pro");
        request.setDescription("Apple smartphone");
        request.setSku("IPH17P-256-BLK");
        request.setPrice(new BigDecimal("1299.99"));
        request.setCurrency("USD");

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(productId, request)
        );

        assertEquals(
                "Product not found",
                exception.getMessage()
        );

        verify(productRepository).findById(productId);
        verify(productRepository, never()).existsBySku(anyString());
        verifyNoInteractions(productMapper);
    }


    @Test
    void shouldThrowExceptionWhenUpdatingWithExistingSku() {

        Long productId = 1L;

        Product product = new Product(
                "iPhone 17 Pro",
                "Apple smartphone",
                "IPH17P-256-BLK",
                new BigDecimal("1299.99"),
                "USD"
        );

        ProductRequest request = new ProductRequest();
        request.setName("Samsung S26");
        request.setDescription("Samsung smartphone");
        request.setSku("SAM-S26-BLK");
        request.setPrice(new BigDecimal("999.99"));
        request.setCurrency("USD");

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(productRepository.existsBySku(request.getSku()))
                .thenReturn(true);

        ProductAlreadyExistException exception = assertThrows(
                ProductAlreadyExistException.class,
                () -> productService.updateProduct(productId, request)
        );

        assertEquals(
                "Product with SKU " + request.getSku() + " already exists",
                exception.getMessage()
        );

        verify(productRepository).findById(productId);
        verify(productRepository).existsBySku(request.getSku());
        verifyNoInteractions(productMapper);
    }


    @Test
    void shouldActivateProductSuccessfully() {

        Long productId = 1L;

        Product product = new Product(
                "iPhone 17 Pro",
                "Apple smartphone",
                "IPH17P-256-BLK",
                new BigDecimal("1299.99"),
                "USD"
        );

        product.deactivate();

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        productService.activateProduct(productId);

        assertEquals(
                ProductStatus.ACTIVE,
                product.getStatus()
        );

        verify(productRepository).findById(productId);
    }


    @Test
    void shouldThrowExceptionWhenActivatingNonExistingProduct() {

        Long productId = 999L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.activateProduct(productId)
        );

        verify(productRepository).findById(productId);
    }


    @Test
    void shouldDeactivateProductSuccessfully() {

        Long productId = 1L;

        Product product = new Product(
                "iPhone 17 Pro",
                "Apple smartphone",
                "IPH17P-256-BLK",
                new BigDecimal("1299.99"),
                "USD"
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        productService.deactivateProduct(productId);

        assertEquals(
                ProductStatus.INACTIVE,
                product.getStatus()
        );

        verify(productRepository).findById(productId);
    }


    @Test
    void shouldThrowExceptionWhenDeactivatingNonExistingProduct() {

        Long productId = 999L;

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deactivateProduct(productId)
        );

        verify(productRepository).findById(productId);
    }
}