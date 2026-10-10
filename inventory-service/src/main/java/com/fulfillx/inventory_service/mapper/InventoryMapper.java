package com.fulfillx.inventory_service.mapper;

import com.fulfillx.inventory_service.domain.model.InventoryItem;
import com.fulfillx.inventory_service.dto.InventoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    InventoryResponse toInventoryResponse(InventoryItem inventoryItem);
}
