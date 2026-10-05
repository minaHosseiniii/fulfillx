package com.fulfillx.customer.mapper;

import com.fulfillx.customer.domain.model.Customer;
import com.fulfillx.customer.dto.CustomerResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponse toCustomerResponse(Customer customer);
}
