package com.customer.api.application.port.in;

import com.customer.api.domain.CustomerId;

public interface DeleteCustomerUseCase {
    void execute(CustomerId customerId);
}