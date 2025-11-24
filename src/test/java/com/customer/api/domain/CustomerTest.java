package com.customer.api.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    @Test
    void createAndUpdateCustomer() {
        CustomerId id = CustomerId.generate();
        Cpf cpf = new Cpf("52998224725");
        Customer customer = new Customer(id, "Joao", cpf, "joao@email.com", "123456789", LocalDateTime.now());
        assertEquals("Joao", customer.name());
        customer.update("Joao Silva", "joao.silva@email.com", "987654321");
        assertEquals("Joao Silva", customer.name());
        assertEquals("joao.silva@email.com", customer.email());
        assertEquals("987654321", customer.phone());
    }

    @Test
    void updateCpf() {
        CustomerId id = CustomerId.generate();
        Cpf cpf = new Cpf("52998224725");
        Customer customer = new Customer(id, "Maria", cpf, "maria@email.com", "123456789", LocalDateTime.now());
        customer.update("Maria Souza", "14538220620", "maria.souza@email.com", "111222333");
        assertEquals("Maria Souza", customer.name());
        assertEquals("14538220620", customer.cpf().value());
    }

    @Test
    void deleteCustomer() {
        CustomerId id = CustomerId.generate();
        Cpf cpf = new Cpf("52998224725");
        Customer customer = new Customer(id, "Ana", cpf, "ana@email.com", "123456789", LocalDateTime.now());
        assertTrue(customer.active());
        customer.delete();
        assertFalse(customer.active());
        assertNotNull(customer.deletedAt());
    }
}

