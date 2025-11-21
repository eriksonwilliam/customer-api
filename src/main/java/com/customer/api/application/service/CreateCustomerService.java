package com.customer.api.application.service;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.port.in.CreateCustomerCommand;
import com.customer.api.application.port.in.CreateCustomerUseCase;
import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.application.port.out.SaveCustomerPort;
import com.customer.api.domain.Cpf;
import com.customer.api.domain.Customer;
import com.customer.api.domain.CustomerId;
import com.customer.api.domain.exception.CustomerAlreadyExistsException;
import com.customer.api.domain.exception.EmailAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
class CreateCustomerService implements CreateCustomerUseCase {

    private final LoadCustomerPort loadCustomerPort;
    private final SaveCustomerPort saveCustomerPort;

    CreateCustomerService(LoadCustomerPort loadCustomerPort, SaveCustomerPort saveCustomerPort) {
        this.loadCustomerPort = loadCustomerPort;
        this.saveCustomerPort = saveCustomerPort;
    }

    @Override
    public CustomerId execute(CreateCustomerCommand command) {
        Cpf cpf = new Cpf(command.cpf());

        if (loadCustomerPort.existsByCpf(cpf)) {
            throw new CustomerAlreadyExistsException(command.cpf());
        }
        if (loadCustomerPort.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        Customer customer = new Customer(
                CustomerId.generate(),
                command.name(),
                cpf,
                command.email(),
                command.phone(),
                LocalDateTime.now()
        );

        saveCustomerPort.save(customer);

        return customer.id();
    }
}