package com.fulfillx.customer.service;

import com.fulfillx.customer.dto.CustomerRequest;
import com.fulfillx.customer.dto.CustomerResponse;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse findCustomerById(Long customerId);
    CustomerResponse updateCustomer(Long customerId, CustomerRequest request);
    void activateCustomer(Long customerId);

    void deactivateCustomer(Long customerId);
}
