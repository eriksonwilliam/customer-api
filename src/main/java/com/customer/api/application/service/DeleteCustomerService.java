package com.customer.api.application.service;

import com.customer.api.application.port.in.DeleteCustomerUseCase;
import com.customer.api.application.port.out.DeleteCustomerPort;
import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.domain.CustomerId;
import com.customer.api.domain.exception.CustomerNotFoundException;
import com.customer.api.messaging.CustomerEvent;
import com.customer.api.messaging.DomainEventPublisher;
import com.customer.api.messaging.DomainEventType;
import org.springframework.stereotype.Service;

@Service
class DeleteCustomerService implements DeleteCustomerUseCase {
    private final LoadCustomerPort loadCustomerPort;
    private final DeleteCustomerPort deleteCustomerPort;
    private final DomainEventPublisher eventPublisher;
    DeleteCustomerService(LoadCustomerPort loadCustomerPort, DeleteCustomerPort deleteCustomerPort, DomainEventPublisher eventPublisher) {
        this.loadCustomerPort = loadCustomerPort;
        this.deleteCustomerPort = deleteCustomerPort;
        this.eventPublisher = eventPublisher;
    }
    @Override
    public void execute(CustomerId customerId) {
        var customer = loadCustomerPort.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId.value()));

        customer.delete();
        deleteCustomerPort.delete(customer);
        eventPublisher.publish(CustomerEvent.deleted(customer));
    }
}