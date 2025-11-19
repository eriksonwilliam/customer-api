package com.customer.api.application.port.in;

import com.customer.api.domain.CustomerId;

public interface UpdateCustomerUseCase {
    void execute(CustomerId customerId, UpdateCustomerCommand command);
}