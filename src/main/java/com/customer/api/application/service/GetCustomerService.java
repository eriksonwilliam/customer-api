package com.customer.api.application.service;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.port.in.GetCustomerQuery;
import com.customer.api.application.port.in.GetCustomerUseCase;
import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.domain.exception.CustomerNotFoundException;
import org.springframework.stereotype.Service;

@Service
class GetCustomerService implements GetCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;

    GetCustomerService(LoadCustomerPort loadCustomerPort) {
        this.loadCustomerPort = loadCustomerPort;
    }

    @Override
    public CustomerDto execute(GetCustomerQuery query) {
        return loadCustomerPort.findById(query.customerId())
                .map(customer -> new CustomerDto(
                        customer.id().value(),
                        customer.name(),
                        customer.cpf().value(),
                        customer.email(),
                        customer.phone(),
                        customer.createdAt(),
                        customer.updatedAt()
                ))
                .orElseThrow(() -> new CustomerNotFoundException(query.customerId().value()));
    }
}