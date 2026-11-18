package com.fulfillx.customer.controller;

import com.fulfillx.customer.dto.CustomerRequest;
import com.fulfillx.customer.dto.CustomerResponse;
import com.fulfillx.customer.dto.CustomerUpdateRequest;
import com.fulfillx.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
    public ResponseEntity<CustomerResponse> getCustomer(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        String keyCloakUserId = jwt.getSubject();
        boolean isPrivileged = authentication.getAuthorities().stream()
                .anyMatch(authority -> Objects.equals(authority.getAuthority(), "ROLE_SUPPORT") ||
                        Objects.equals(authority.getAuthority(), "ROLE_ADMIN"));
        return ResponseEntity.ok(customerService.findCustomerById(id, keyCloakUserId, isPrivileged));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable("id") Long id,
            @RequestBody @Valid CustomerUpdateRequest customerRequest,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        String keyCloakUserId = jwt.getSubject();
        boolean isPrivileged = authentication.getAuthorities().stream()
                .anyMatch(authority -> Objects.equals(authority.getAuthority(), "ROLE_SUPPORT") ||
                        Objects.equals(authority.getAuthority(), "ROLE_ADMIN"));
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(id, customerRequest, keyCloakUserId, isPrivileged));
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
