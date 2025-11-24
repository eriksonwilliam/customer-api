package com.customer.api.application.service;

import com.customer.api.application.port.in.UpdateCustomerCommand;
import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.application.port.out.UpdateCustomerPort;
import com.customer.api.domain.*;
import com.customer.api.domain.exception.CustomerAlreadyExistsException;
import com.customer.api.domain.exception.EmailAlreadyExistsException;
import com.customer.api.messaging.DomainEventPublisher;
import com.customer.api.messaging.CustomerEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateCustomerServiceTest {

    private final LoadCustomerPort loadPort = mock(LoadCustomerPort.class);
    private final UpdateCustomerPort updatePort = mock(UpdateCustomerPort.class);
    private final DomainEventPublisher eventPublisher = mock(DomainEventPublisher.class);
    private final UpdateCustomerService service = new UpdateCustomerService(loadPort, updatePort, eventPublisher);

    @Test
    void atualizaComSucesso() {
        Customer customer = new Customer(CustomerId.generate(), "Antigo", new Cpf("52998224725"), "antigo@test.com", "1199999", LocalDateTime.now());
        when(loadPort.findById(any())).thenReturn(Optional.of(customer));
        when(loadPort.existsByCpf(any())).thenReturn(false);

        service.execute(customer.id(), new UpdateCustomerCommand("Novo Nome", "52998224725", "novo@test.com", "1188888"));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(updatePort).update(captor.capture());
        assertEquals("Novo Nome", captor.getValue().name());
        assertEquals("novo@test.com", captor.getValue().email());
        verify(eventPublisher).publish(any(CustomerEvent.class));
    }

    @Test
    void naoPermiteAlterarParaCpfExistente() {
        Customer customer = new Customer(CustomerId.generate(), "João", new Cpf("52998224725"), "joao@test.com", null, LocalDateTime.now());
        when(loadPort.findById(any())).thenReturn(Optional.of(customer));
        when(loadPort.existsByCpf(new Cpf("12345678909"))).thenReturn(true);

        UpdateCustomerCommand command = new UpdateCustomerCommand("João", "12345678909", "joao@test.com", null);

        assertThrows(CustomerAlreadyExistsException.class, () -> service.execute(customer.id(), command));
    }

    @Test
    void naoPermiteAlterarParaEmailExistente() {
        Customer customer = new Customer(CustomerId.generate(), "Maria", new Cpf("52998224725"), "maria@test.com", null, LocalDateTime.now());
        when(loadPort.findById(any())).thenReturn(Optional.of(customer));
        when(loadPort.existsByCpf(any())).thenReturn(false);
        when(loadPort.existsByEmail("existente@test.com")).thenReturn(true);

        UpdateCustomerCommand command = new UpdateCustomerCommand("Maria", "52998224725", "existente@test.com", null);

        assertThrows(EmailAlreadyExistsException.class, () -> service.execute(customer.id(), command));
    }

    @Test
    void permiteManterMesmoEmailSemVerificacaoDuplicada() {
        Customer customer = new Customer(CustomerId.generate(), "Carlos", new Cpf("52998224725"), "carlos@test.com", null, LocalDateTime.now());
        when(loadPort.findById(any())).thenReturn(Optional.of(customer));
        when(loadPort.existsByCpf(any())).thenReturn(false);

        UpdateCustomerCommand command = new UpdateCustomerCommand("Carlos Atual", "52998224725", "carlos@test.com", null);
        assertDoesNotThrow(() -> service.execute(customer.id(), command));
        verify(loadPort, never()).existsByEmail("carlos@test.com");
    }

    @Test
    void alteraCpfEEmailSimultaneamenteComSucesso() {
        Customer customer = new Customer(CustomerId.generate(), "Original", new Cpf("52998224725"), "orig@test.com", null, LocalDateTime.now());
        when(loadPort.findById(any())).thenReturn(Optional.of(customer));
        when(loadPort.existsByCpf(new Cpf("14538220620"))).thenReturn(false);
        when(loadPort.existsByEmail("novo@test.com")).thenReturn(false);

        UpdateCustomerCommand cmd = new UpdateCustomerCommand("Original", "14538220620", "novo@test.com", null);
        assertDoesNotThrow(() -> service.execute(customer.id(), cmd));

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(updatePort).update(captor.capture());
        assertEquals("14538220620", captor.getValue().cpf().value());
        assertEquals("novo@test.com", captor.getValue().email());
    }

    @Test
    void alteraCpfEEmailMasConflitosEmAmbos() {
        Customer customer = new Customer(CustomerId.generate(), "Original", new Cpf("52998224725"), "orig@test.com", null, LocalDateTime.now());
        when(loadPort.findById(any())).thenReturn(Optional.of(customer));
        when(loadPort.existsByCpf(new Cpf("14538220620"))).thenReturn(true);
        when(loadPort.existsByEmail("existe@test.com")).thenReturn(true);

        UpdateCustomerCommand cmd = new UpdateCustomerCommand("Original", "14538220620", "existe@test.com", null);
        assertThrows(CustomerAlreadyExistsException.class, () -> service.execute(customer.id(), cmd));
        verify(loadPort, times(1)).existsByCpf(new Cpf("14538220620"));
        verify(loadPort, never()).existsByEmail("existe@test.com");
    }
}

