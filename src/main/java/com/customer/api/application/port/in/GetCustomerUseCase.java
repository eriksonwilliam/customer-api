package com.customer.api.application.port.in;

import com.customer.api.application.dto.CustomerDto;

public interface GetCustomerUseCase {
    CustomerDto execute(GetCustomerQuery query);
}