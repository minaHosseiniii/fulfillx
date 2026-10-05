package com.fulfillx.customer.service.impl;

import com.fulfillx.customer.domain.model.Customer;
import com.fulfillx.customer.dto.CustomerRequest;
import com.fulfillx.customer.dto.CustomerResponse;
import com.fulfillx.customer.exception.CustomerAlreadyExistException;
import com.fulfillx.customer.exception.CustomerNotFoundException;
import com.fulfillx.customer.mapper.CustomerMapper;
import com.fulfillx.customer.repository.CustomerRepository;
import com.fulfillx.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistException("Customer with email " + request.getEmail() + " already exists");
        }
        Customer customer = new Customer(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone()
        );

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toCustomerResponse(savedCustomer);

    }

    @Override
    public CustomerResponse findCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest request) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        if (!customer.getEmail().equals(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistException(
                    "Customer with email " + request.getEmail() + " already exists"
            );
        }
        customer.update(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhone()
                );
        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public void activateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        customer.activate();
    }

    @Override
    public void deactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        customer.deactivate();
    }

}
