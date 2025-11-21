package com.customer.api.application.service;

import com.customer.api.application.port.in.DeleteCustomerUseCase;
import com.customer.api.application.port.out.DeleteCustomerPort;
import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.domain.Customer;
import com.customer.api.domain.CustomerId;
import com.customer.api.domain.Cpf;
import com.customer.api.domain.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteCustomerServiceTest {

    private final LoadCustomerPort loadPort = mock(LoadCustomerPort.class);
    private final DeleteCustomerPort deletePort = mock(DeleteCustomerPort.class);
    private final DeleteCustomerUseCase service = new DeleteCustomerService(loadPort, deletePort);

    @Test
    void deletaClienteComSucesso() {
        CustomerId id = CustomerId.generate();
        Customer customer = new Customer(
                id,
                "Maria Silva",
                new Cpf("52998224725"),
                "maria@test.com",
                "11999999999",
                LocalDateTime.now()
        );

        when(loadPort.findById(id)).thenReturn(Optional.of(customer));

        service.execute(id);

        assertFalse(customer.active());
        assertNotNull(customer.deletedAt());

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(deletePort).delete(captor.capture());
        Customer deleted = captor.getValue();
        assertFalse(deleted.active());
        assertNotNull(deleted.deletedAt());
    }

    @Test
    void lançaExcecaoQuandoClienteNaoExiste() {
        CustomerId id = CustomerId.generate();

        when(loadPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.execute(id));
        verify(deletePort, never()).delete(any());
    }
}