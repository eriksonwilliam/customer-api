package com.customer.api.adapter.inbound.controller;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.dto.PageResponse;
import com.customer.api.application.port.in.*;
import com.customer.api.domain.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerResourceTest {
    private CreateCustomerUseCase createCustomerUseCase;
    private GetCustomerUseCase getCustomerUseCase;
    private ListCustomersUseCase listCustomersUseCase;
    private UpdateCustomerUseCase updateCustomerUseCase;
    private DeleteCustomerUseCase deleteCustomerUseCase;
    private CustomerResource resource;

    @BeforeEach
    void setUp() {
        createCustomerUseCase = mock(CreateCustomerUseCase.class);
        getCustomerUseCase = mock(GetCustomerUseCase.class);
        listCustomersUseCase = mock(ListCustomersUseCase.class);
        updateCustomerUseCase = mock(UpdateCustomerUseCase.class);
        deleteCustomerUseCase = mock(DeleteCustomerUseCase.class);
        resource = new CustomerResource(createCustomerUseCase, getCustomerUseCase, listCustomersUseCase, updateCustomerUseCase, deleteCustomerUseCase);
    }

    @Test
    void create_deveRetornarCreatedComLocation() {
//        var command = mock(CreateCustomerCommand.class);
        CreateCustomerCommand command = new CreateCustomerCommand("Nome", "12345678901", "email@test.com", "11999999999");
        var id = new CustomerId(UUID.randomUUID());
        when(createCustomerUseCase.execute(command)).thenReturn(id);
        ResponseEntity<Void> response = resource.create(command);
        assertEquals(201, response.getStatusCodeValue());
        assertTrue(response.getHeaders().getLocation().toString().contains(id.value().toString()));
    }

    @Test
    void findById_deveRetornarCustomerDto() {
        UUID uuid = UUID.randomUUID();
        CustomerDto dto = new CustomerDto(uuid, "Nome", "12345678901", "email@test.com", "11999999999", null, null);
        when(getCustomerUseCase.execute(any())).thenReturn(dto);
        ResponseEntity<CustomerDto> response = resource.findById(uuid);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void list_deveRetornarPageResponse() {
        Pageable pageable = PageRequest.of(0, 2);
        List<CustomerDto> dtos = List.of(
            new CustomerDto(UUID.randomUUID(), "Nome1", "12345678901", "email1@test.com", "11999999999", null, null),
            new CustomerDto(UUID.randomUUID(), "Nome2", "12345678902", "email2@test.com", "11988888888", null, null)
        );
        Page<CustomerDto> page = new PageImpl<>(dtos, pageable, 2);
        when(listCustomersUseCase.execute(any())).thenReturn(page);
        ResponseEntity<PageResponse<CustomerDto>> response = resource.list(null, pageable);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().content().size());
    }

    @Test
    void update_deveRetornarNoContent() {
        UUID uuid = UUID.randomUUID();
//        UpdateCustomerCommand command = mock(UpdateCustomerCommand.class);
        UpdateCustomerCommand command = new UpdateCustomerCommand("Nome", "12345678901", "email@test.com", "11999999999");
        ResponseEntity<Void> response = resource.update(uuid, command);
        assertEquals(204, response.getStatusCodeValue());
        verify(updateCustomerUseCase).execute(any(), eq(command));
    }

    @Test
    void delete_deveRetornarNoContent() {
        UUID uuid = UUID.randomUUID();
        ResponseEntity<Void> response = resource.delete(uuid);
        assertEquals(204, response.getStatusCodeValue());
        verify(deleteCustomerUseCase).execute(any());
    }

    @Test
    void create_deveLancarExcecaoQuandoUseCaseFalha() {
//        var command = mock(CreateCustomerCommand.class);
        CreateCustomerCommand command = new CreateCustomerCommand("Nome", "12345678901", "email@test.com", "11999999999");
        when(createCustomerUseCase.execute(command)).thenThrow(new RuntimeException("Erro ao criar"));
        assertThrows(RuntimeException.class, () -> resource.create(command));
    }

    @Test
    void findById_deveLancarExcecaoQuandoNaoEncontrado() {
        UUID uuid = UUID.randomUUID();
        when(getCustomerUseCase.execute(any())).thenThrow(new RuntimeException("Não encontrado"));
        assertThrows(RuntimeException.class, () -> resource.findById(uuid));
    }

    @Test
    void list_deveRetornarListaVazia() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<CustomerDto> page = new PageImpl<>(List.of(), pageable, 0);
        when(listCustomersUseCase.execute(any())).thenReturn(page);
        ResponseEntity<PageResponse<CustomerDto>> response = resource.list(null, pageable);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().content().isEmpty());
    }

    @Test
    void update_deveLancarExcecaoQuandoUseCaseFalha() {
        UUID uuid = UUID.randomUUID();
//        UpdateCustomerCommand command = mock(UpdateCustomerCommand.class);
        UpdateCustomerCommand command = new UpdateCustomerCommand("Nome", "12345678901", "email@test.com", "11999999999");
        doThrow(new RuntimeException("Erro ao atualizar")).when(updateCustomerUseCase).execute(any(), eq(command));
        assertThrows(RuntimeException.class, () -> resource.update(uuid, command));
    }

    @Test
    void delete_deveLancarExcecaoQuandoUseCaseFalha() {
        UUID uuid = UUID.randomUUID();
        doThrow(new RuntimeException("Erro ao deletar")).when(deleteCustomerUseCase).execute(any());
        assertThrows(RuntimeException.class, () -> resource.delete(uuid));
    }
}
