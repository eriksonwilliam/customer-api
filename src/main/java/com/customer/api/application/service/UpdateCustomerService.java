package com.customer.api.application.service;

import com.customer.api.application.port.in.UpdateCustomerCommand;
import com.customer.api.application.port.in.UpdateCustomerUseCase;
import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.application.port.out.UpdateCustomerPort;
import com.customer.api.domain.Cpf;
import com.customer.api.domain.Customer;
import com.customer.api.domain.CustomerId;
import com.customer.api.domain.exception.CustomerAlreadyExistsException;
import com.customer.api.domain.exception.CustomerNotFoundException;
import org.springframework.stereotype.Service;

@Service
class UpdateCustomerService implements UpdateCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final UpdateCustomerPort updateCustomerPort;

    UpdateCustomerService(LoadCustomerPort loadCustomerPort, UpdateCustomerPort updateCustomerPort) {
        this.loadCustomerPort = loadCustomerPort;
        this.updateCustomerPort = updateCustomerPort;
    }

    @Override
    public void execute(CustomerId customerId, UpdateCustomerCommand command) {
        Customer customer = loadCustomerPort.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId.value()));

        Cpf newCpf = new Cpf(command.cpf());

        if (!customer.cpf().equals(newCpf) && loadCustomerPort.existsByCpf(newCpf)) {
            throw new CustomerAlreadyExistsException(command.cpf());
        }

        customer.update(command.name(), command.email(), command.phone());

        updateCustomerPort.update(customer);
    }
}