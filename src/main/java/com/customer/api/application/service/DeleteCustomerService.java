package com.customer.api.application.service;

import com.customer.api.application.port.in.DeleteCustomerUseCase;
import com.customer.api.application.port.out.DeleteCustomerPort;
import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.domain.CustomerId;
import com.customer.api.domain.exception.CustomerNotFoundException;
import org.springframework.stereotype.Service;

@Service
class DeleteCustomerService implements DeleteCustomerUseCase {
    private final LoadCustomerPort loadCustomerPort;
    private final DeleteCustomerPort deleteCustomerPort;
    DeleteCustomerService(LoadCustomerPort loadCustomerPort, DeleteCustomerPort deleteCustomerPort) {
        this.loadCustomerPort = loadCustomerPort;
        this.deleteCustomerPort = deleteCustomerPort;
    }
    @Override
    public void execute(CustomerId customerId) {
        var customer = loadCustomerPort.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId.value()));

        customer.delete();
        deleteCustomerPort.delete(customer);
    }
}