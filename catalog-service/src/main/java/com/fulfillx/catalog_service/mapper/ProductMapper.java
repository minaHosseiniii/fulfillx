package com.fulfillx.catalog_service.mapper;

import com.fulfillx.catalog_service.domain.model.Product;
import com.fulfillx.catalog_service.dto.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toProductResponse(Product product);
}