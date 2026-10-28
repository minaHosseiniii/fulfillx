package com.fulfillx.order.client;

import com.fulfillx.order.client.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {
    @GetMapping("/api/v1/customers/{id}")
    CustomerResponse getCustomer(@PathVariable("id") Long customerId);
}
