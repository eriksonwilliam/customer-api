package com.customer.api.application.port.out;

import com.customer.api.domain.Customer;

public interface DeleteCustomerPort {
    void delete(Customer customer);
}