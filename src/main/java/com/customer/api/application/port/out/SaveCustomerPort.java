package com.customer.api.application.port.out;

import com.customer.api.domain.Customer;

public interface SaveCustomerPort {
    void save(Customer customer);
}