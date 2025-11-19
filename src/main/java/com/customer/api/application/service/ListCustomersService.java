package com.customer.api.application.service;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.port.in.ListCustomersQuery;
import com.customer.api.application.port.in.ListCustomersUseCase;
import com.customer.api.application.port.out.LoadCustomerPagePort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
class ListCustomersService implements ListCustomersUseCase {

    private final LoadCustomerPagePort loadCustomerPagePort;

    ListCustomersService(LoadCustomerPagePort loadCustomerPagePort) {
        this.loadCustomerPagePort = loadCustomerPagePort;
    }

    @Override
    public Page<CustomerDto> execute(ListCustomersQuery query) {
        return loadCustomerPagePort.findAll(query.search(), query.pageable())
                .map(customer -> new CustomerDto(
                        customer.id().value(),
                        customer.name(),
                        customer.cpf().value(),
                        customer.email(),
                        customer.phone(),
                        customer.createdAt(),
                        customer.updatedAt()
                ));
    }
}