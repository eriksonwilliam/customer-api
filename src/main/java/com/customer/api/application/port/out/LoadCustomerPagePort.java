package com.customer.api.application.port.out;

import com.customer.api.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadCustomerPagePort {
    Page<Customer> findAll(String search, Pageable pageable);
}