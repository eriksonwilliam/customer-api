package com.customer.api.application.service;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.port.in.GetCustomerQuery;
import com.customer.api.application.port.in.GetCustomerUseCase;
import com.customer.api.application.port.out.LoadCustomerPort;
import com.customer.api.domain.Cpf;
import com.customer.api.domain.Customer;
import com.customer.api.domain.CustomerId;
import com.customer.api.domain.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetCustomerServiceTest {

    private final LoadCustomerPort loadPort = mock(LoadCustomerPort.class);
    private final GetCustomerUseCase service = new GetCustomerService(loadPort);

    @Test
    void buscaClientePorIdComSucesso() {
        CustomerId id = CustomerId.from("123e4567-e89b-12d3-a456-426614174000");
        Customer customer = new Customer(
                id,
                "Pedro Lima",
                new Cpf("52998224725"),
                "pedro@test.com",
                "11955554444",
                LocalDateTime.of(2025, 1, 1, 10, 0)
        );

        when(loadPort.findById(id)).thenReturn(Optional.of(customer));

        CustomerDto dto = service.execute(new GetCustomerQuery(id));

        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), dto.id());
        assertEquals("Pedro Lima", dto.name());
        assertEquals("52998224725", dto.cpf());
        assertFalse(dto.createdAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void lançaExcecaoQuandoClienteNaoExiste() {
        CustomerId id = CustomerId.generate();
        when(loadPort.findById(id)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> service.execute(new GetCustomerQuery(id)));
    }
}