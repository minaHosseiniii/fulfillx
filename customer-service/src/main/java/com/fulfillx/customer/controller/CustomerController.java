package com.fulfillx.customer.controller;

import com.fulfillx.customer.dto.CustomerRequest;
import com.fulfillx.customer.dto.CustomerResponse;
import com.fulfillx.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody @Valid CustomerRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.findCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable("id") Long id, @RequestBody @Valid CustomerRequest customerRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(id, customerRequest));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCustomer(@PathVariable("id") Long id) {
        customerService.activateCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateCustomer(@PathVariable("id") Long id) {
        customerService.deactivateCustomer(id);
        return ResponseEntity.noContent().build();
    }

}
