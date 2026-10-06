package com.fulfillx.customer.service.impl;

import com.fulfillx.customer.domain.model.Customer;
import com.fulfillx.customer.dto.CustomerRequest;
import com.fulfillx.customer.dto.CustomerResponse;
import com.fulfillx.customer.exception.CustomerAlreadyExistException;
import com.fulfillx.customer.exception.CustomerNotFoundException;
import com.fulfillx.customer.mapper.CustomerMapper;
import com.fulfillx.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void shouldCreateCustomerSuccessfully() {
        CustomerRequest request = new CustomerRequest(
                "Mina",
                "Hosseini",
                "mina@gmail.com",
                "09120000000"
        );

        Customer customer = new Customer(
                "Mina",
                "Hosseini",
                "mina@gmail.com",
                "09120000000"
        );

        CustomerResponse response = new CustomerResponse();

        when(customerRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);

        when(customerMapper.toCustomerResponse(customer))
                .thenReturn(response);

        CustomerResponse result = customerService.createCustomer(request);

        assertSame(response, result);

        verify(customerRepository)
                .existsByEmail(request.getEmail());

        verify(customerRepository)
                .save(any(Customer.class));

        verify(customerMapper)
                .toCustomerResponse(customer);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        CustomerRequest request = new CustomerRequest(
                "Mina",
                "Hosseini",
                "mina@gmail.com",
                "09120000000"
        );

        when(customerRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                CustomerAlreadyExistException.class,
                () -> customerService.createCustomer(request)
        );

        verify(customerRepository)
                .existsByEmail(request.getEmail());

        verify(customerRepository, never())
                .save(any(Customer.class));

        verifyNoInteractions(customerMapper);
    }

    @Test
    void shouldFindCustomerByIdSuccessfully() {
        Long customerId = 1L;

        Customer customer = new Customer(
                "Mina",
                "Hosseini",
                "mina@gmail.com",
                "09120000000"
        );

        CustomerResponse response = new CustomerResponse();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toCustomerResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                customerService.findCustomerById(customerId);

        assertSame(response, result);

        verify(customerRepository)
                .findById(customerId);

        verify(customerMapper)
                .toCustomerResponse(customer);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        Long customerId = 999L;

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.findCustomerById(customerId)
        );

        verify(customerRepository)
                .findById(customerId);

        verifyNoInteractions(customerMapper);
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        Long customerId = 1L;

        Customer customer = new Customer(
                "Mina",
                "Hosseini",
                "old@gmail.com",
                "09120000000"
        );

        CustomerRequest request = new CustomerRequest(
                "Mina Updated",
                "Hosseini",
                "new@gmail.com",
                "09121111111"
        );

        CustomerResponse response = new CustomerResponse();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(customerMapper.toCustomerResponse(customer))
                .thenReturn(response);

        CustomerResponse result =
                customerService.updateCustomer(customerId, request);

        assertSame(response, result);

        assertEquals("Mina Updated", customer.getFirstName());
        assertEquals("new@gmail.com", customer.getEmail());
        assertEquals("09121111111", customer.getPhone());

        verify(customerRepository)
                .findById(customerId);

        verify(customerRepository)
                .existsByEmail(request.getEmail());

        verify(customerMapper)
                .toCustomerResponse(customer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithExistingEmail() {
        Long customerId = 1L;

        Customer customer = new Customer(
                "Mina",
                "Hosseini",
                "old@gmail.com",
                "09120000000"
        );

        CustomerRequest request = new CustomerRequest(
                "Mina",
                "Hosseini",
                "another@gmail.com",
                "09121111111"
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        assertThrows(
                CustomerAlreadyExistException.class,
                () -> customerService.updateCustomer(customerId, request)
        );

        verify(customerRepository)
                .findById(customerId);

        verify(customerRepository)
                .existsByEmail(request.getEmail());

        verify(customerMapper, never())
                .toCustomerResponse(any());
    }

    @Test
    void shouldActivateCustomer() {
        Long customerId = 1L;

        Customer customer = new Customer(
                "Mina",
                "Hosseini",
                "mina@gmail.com",
                "09120000000"
        );

        customer.deactivate();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        customerService.activateCustomer(customerId);

        assertEquals(
                com.fulfillx.customer.domain.model.CustomerStatus.ACTIVE,
                customer.getStatus()
        );

        verify(customerRepository)
                .findById(customerId);
    }

    @Test
    void shouldDeactivateCustomer() {
        Long customerId = 1L;

        Customer customer = new Customer(
                "Mina",
                "Hosseini",
                "mina@gmail.com",
                "09120000000"
        );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        customerService.deactivateCustomer(customerId);
        assertEquals(
                com.fulfillx.customer.domain.model.CustomerStatus.INACTIVE,
                customer.getStatus()
        );

        verify(customerRepository)
                .findById(customerId);
    }
}
