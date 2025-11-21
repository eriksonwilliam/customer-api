package com.customer.api.application.service;

import com.customer.api.application.dto.CustomerDto;
import com.customer.api.application.port.in.ListCustomersQuery;
import com.customer.api.application.port.in.ListCustomersUseCase;
import com.customer.api.application.port.out.LoadCustomerPagePort;
import com.customer.api.domain.Cpf;
import com.customer.api.domain.Customer;
import com.customer.api.domain.CustomerId;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ListCustomersServiceTest {

    private final LoadCustomerPagePort loadPagePort = mock(LoadCustomerPagePort.class);
    private final ListCustomersUseCase service = new ListCustomersService(loadPagePort);

    @Test
    void listaClientesComPaginacao() {
        Customer c1 = new Customer(CustomerId.generate(), "Ana Souza", new Cpf("52998224725"), "ana@test.com", "11911112222", LocalDateTime.now());
        Customer c2 = new Customer(CustomerId.generate(), "Carlos Silva", new Cpf("98765432100"), "carlos@test.com", "11933334444", LocalDateTime.now());

        Page<Customer> page = new PageImpl<>(List.of(c1, c2));

        when(loadPagePort.findAll(any(), any(Pageable.class))).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<CustomerDto> result = service.execute(new ListCustomersQuery(null, pageable));

        assertEquals(2, result.getTotalElements());
        assertEquals("Ana Souza", result.getContent().get(0).name());
        assertEquals("carlos@test.com", result.getContent().get(1).email());
    }

    @Test
    void retornaPaginaVaziaQuandoNenhumClienteAtivo() {
        when(loadPagePort.findAll(any(), any())).thenReturn(Page.empty());

        Page<CustomerDto> result = service.execute(new ListCustomersQuery(null, Pageable.unpaged()));

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void aplicaFiltroDeBuscaPorNomeOuEmail() {
        Customer customer = new Customer(CustomerId.generate(), "João Pereira", new Cpf("12345678909"), "joao.pereira@test.com", null, LocalDateTime.now());

        Page<Customer> page = new PageImpl<>(List.of(customer));
        when(loadPagePort.findAll(eq("joao"), any())).thenReturn(page);

        Page<CustomerDto> result = service.execute(new ListCustomersQuery("joao", Pageable.unpaged()));

        assertEquals(1, result.getTotalElements());
        assertEquals("João Pereira", result.getContent().get(0).name());
    }

    @Test
    void ignoraClientesComSoftDelete() {
        Customer active = new Customer(CustomerId.generate(), "Ativo", new Cpf("52998224725"), "ativo@test.com", null, LocalDateTime.now());

        Customer deleted = new Customer(CustomerId.generate(), "Deletado", new Cpf("98765432100"), "deletado@test.com", null, LocalDateTime.now());
        deleted.delete();

        Page<Customer> page = new PageImpl<>(List.of(active));

        when(loadPagePort.findAll(any(), any())).thenReturn(page);

        Page<CustomerDto> result = service.execute(new ListCustomersQuery(null, Pageable.unpaged()));

        assertEquals(1, result.getTotalElements());
        assertEquals("Ativo", result.getContent().get(0).name());
    }
}