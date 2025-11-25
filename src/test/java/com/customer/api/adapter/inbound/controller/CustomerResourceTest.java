package com.customer.api.adapter.inbound.controller;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.dto.PageResponse;
import com.customer.api.application.port.in.*;
import com.customer.api.domain.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        resource = new CustomerResource(
                createCustomerUseCase,
                getCustomerUseCase,
                listCustomersUseCase,
                updateCustomerUseCase,
                deleteCustomerUseCase
        );
    }

    @Test
    void create_returnsCreated() {
        CreateCustomerCommand command = new CreateCustomerCommand("João", "12345678901", "joao@email.com", "11999999999");
        CustomerId customerId = CustomerId.from(UUID.randomUUID().toString());
        when(createCustomerUseCase.execute(any(CreateCustomerCommand.class))).thenReturn(customerId);

        ResponseEntity<Void> response = resource.create(command);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains("/api/v1/customers/"));
        verify(createCustomerUseCase).execute(command);
    }

    @Test
    void findById_returnsCustomer() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        CustomerDto dto = new CustomerDto(id, "João", "12345678901", "joao@email.com", "11999999999", now, now);
        when(getCustomerUseCase.execute(any(GetCustomerQuery.class))).thenReturn(dto);

        ResponseEntity<CustomerDto> response = resource.findById(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(dto, response.getBody());
        verify(getCustomerUseCase).execute(any(GetCustomerQuery.class));
    }

    @Test
    void list_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        List<CustomerDto> list = List.of(new CustomerDto(id, "João", "12345678901", "joao@email.com", "11999999999", now, now));
        PageImpl<CustomerDto> page = new PageImpl<>(list, pageable, 1);
        when(listCustomersUseCase.execute(any(ListCustomersQuery.class))).thenReturn(page);

        ResponseEntity<PageResponse<CustomerDto>> response = resource.list("joao", pageable);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());
        verify(listCustomersUseCase).execute(any(ListCustomersQuery.class));
    }

    @Test
    void update_returnsNoContent() {
        UUID id = UUID.randomUUID();
        UpdateCustomerCommand command = new UpdateCustomerCommand("João Silva", "12345678901", "joao@email.com", "11999999999");
        doNothing().when(updateCustomerUseCase).execute(any(CustomerId.class), any(UpdateCustomerCommand.class));

        ResponseEntity<Void> response = resource.update(id, command);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(updateCustomerUseCase).execute(any(CustomerId.class), eq(command));
    }

    @Test
    void delete_returnsNoContent() {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteCustomerUseCase).execute(any(CustomerId.class));

        ResponseEntity<Void> response = resource.delete(id);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(deleteCustomerUseCase).execute(any(CustomerId.class));
    }
}