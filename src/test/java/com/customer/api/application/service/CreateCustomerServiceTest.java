package com.customer.api.application.service;

import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.application.port.out.SaveCustomerPort;
import com.customer.api.application.port.in.CreateCustomerCommand;
import com.customer.api.domain.Cpf;
import com.customer.api.domain.CustomerId;
import com.customer.api.domain.exception.CustomerAlreadyExistsException;
import com.customer.api.messaging.DomainEventPublisher;
import com.customer.api.messaging.CustomerEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateCustomerServiceTest {

    private final LoadCustomerPort loadPort = mock(LoadCustomerPort.class);
    private final SaveCustomerPort savePort = mock(SaveCustomerPort.class);
    private final DomainEventPublisher eventPublisher = mock(DomainEventPublisher.class);
    private final CreateCustomerService service = new CreateCustomerService(loadPort, savePort, eventPublisher);

    @Test
    void criaClienteComSucesso() {
        when(loadPort.existsByCpf(any())).thenReturn(false);

        CreateCustomerCommand command = new CreateCustomerCommand("João", "52998224725", "joao@test.com", "1199999");

        CustomerId id = service.execute(command);

        assertNotNull(id);

        ArgumentCaptor<com.customer.api.domain.Customer> captor = ArgumentCaptor.forClass(com.customer.api.domain.Customer.class);
        verify(savePort).save(captor.capture());
        com.customer.api.domain.Customer saved = captor.getValue();

        assertEquals("João", saved.name());
        assertEquals("52998224725", saved.cpf().value());
        verify(eventPublisher).publish(any(CustomerEvent.class));
    }

    @Test
    void naoPermiteCpfDuplicado() {
        when(loadPort.existsByCpf(any(Cpf.class))).thenReturn(true);

        CreateCustomerCommand command = new CreateCustomerCommand("João", "52998224725", "joao@test.com", null);

        assertThrows(CustomerAlreadyExistsException.class, () -> service.execute(command));
    }
}