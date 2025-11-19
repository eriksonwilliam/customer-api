package com.customer.api.application.port.out;

import com.customer.api.domain.Customer;

public interface UpdateCustomerPort {
    void update(Customer customer);
}