package com.fulfillx.notification_service.client;

import com.fulfillx.notification_service.client.dto.CustomerServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "customer-service",
        path = "/api/v1/customers"
)
public interface CustomerServiceClient {
    @GetMapping("/{id}")
    CustomerServiceResponse getCustomerById(@PathVariable("id") Long id);

}
