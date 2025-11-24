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
import com.customer.api.domain.exception.EmailAlreadyExistsException;
import com.customer.api.messaging.CustomerEvent;
import com.customer.api.messaging.DomainEventPublisher;
import com.customer.api.messaging.DomainEventType;
import org.springframework.stereotype.Service;

@Service
class UpdateCustomerService implements UpdateCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final UpdateCustomerPort updateCustomerPort;
    private final DomainEventPublisher eventPublisher;

    UpdateCustomerService(LoadCustomerPort loadCustomerPort, UpdateCustomerPort updateCustomerPort, DomainEventPublisher eventPublisher) {
        this.loadCustomerPort = loadCustomerPort;
        this.updateCustomerPort = updateCustomerPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(CustomerId customerId, UpdateCustomerCommand command) {
        Customer customer = loadCustomerPort.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId.value()));

        Cpf newCpf = new Cpf(command.cpf());

        boolean cpfChanged = !customer.cpf().equals(newCpf);
        boolean emailChanged = !customer.email().equals(command.email());

        if (cpfChanged && loadCustomerPort.existsByCpf(newCpf)) {
            throw new CustomerAlreadyExistsException(command.cpf());
        }
        if (emailChanged && loadCustomerPort.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        if (cpfChanged) {
            customer.update(command.name(), command.cpf(), command.email(), command.phone());
        } else {
            customer.update(command.name(), command.email(), command.phone());
        }

        updateCustomerPort.update(customer);
        eventPublisher.publish(CustomerEvent.updated(customer));
    }
}