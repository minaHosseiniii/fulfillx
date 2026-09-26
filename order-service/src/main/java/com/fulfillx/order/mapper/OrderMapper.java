package com.fulfillx.order.mapper;

import com.fulfillx.order.domain.dto.*;
import com.fulfillx.order.domain.model.Money;
import com.fulfillx.order.domain.model.Order;
import com.fulfillx.order.domain.model.OrderItem;
import com.fulfillx.order.domain.model.ShippingAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    ShippingAddress toShippingAddress(ShippingAddressRequest request);
    ShippingAddressResponse toShippingAddressResponse(ShippingAddress shippingAddress);

    @Mapping(source = "unitPriceSnapshot.amount", target = "unitPrice")
    @Mapping(source = "unitPriceSnapshot.currency", target = "currency")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    MoneyResponse toMoneyResponse(Money money);

    @Mapping(source = "orderStatus", target = "status")
    OrderResponse toOrderResponse(Order order);
}
