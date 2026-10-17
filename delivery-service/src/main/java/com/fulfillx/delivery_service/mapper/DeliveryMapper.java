package com.fulfillx.delivery_service.mapper;

import com.fulfillx.delivery_service.domain.model.Delivery;
import com.fulfillx.delivery_service.dto.DeliveryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    @Mapping(target = "recipientName",
            source = "deliveryAddress.recipientName")
    @Mapping(target = "phoneNumber",
            source = "deliveryAddress.phoneNumber")
    @Mapping(target = "province",
            source = "deliveryAddress.province")
    @Mapping(target = "city",
            source = "deliveryAddress.city")
    @Mapping(target = "addressLine",
            source = "deliveryAddress.addressLine")
    @Mapping(target = "postalCode",
            source = "deliveryAddress.postalCode")
    DeliveryResponse toDeliveryResponse(Delivery delivery);
}
