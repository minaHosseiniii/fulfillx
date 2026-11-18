package com.fulfillx.customer.service;

import com.fulfillx.customer.dto.CustomerRequest;
import com.fulfillx.customer.dto.CustomerResponse;
import com.fulfillx.customer.dto.CustomerUpdateRequest;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse findCustomerById(Long customerId, String keycloakUserId, boolean isPrivileged);

    CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request, String keycloakUserId, boolean isPrivileged);
    void activateCustomer(Long customerId);

    void deactivateCustomer(Long customerId);
}
