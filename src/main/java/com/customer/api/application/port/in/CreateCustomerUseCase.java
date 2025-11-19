package com.customer.api.application.port.in;

import com.customer.api.domain.CustomerId;

public interface CreateCustomerUseCase {
    CustomerId execute(CreateCustomerCommand command);
}