package com.customer.api.application.port.in;

import com.customer.api.application.dto.CustomerDto;
import org.springframework.data.domain.Page;

public interface ListCustomersUseCase {
    Page<CustomerDto> execute(ListCustomersQuery query);
}