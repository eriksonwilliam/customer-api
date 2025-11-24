package com.customer.api.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

class DomainExceptionTest {
    @Test
    void customerNotFoundException_message() {
        UUID id = UUID.randomUUID();
        CustomerNotFoundException ex = new CustomerNotFoundException(id);
        assertTrue(ex.getMessage().contains(id.toString()));
    }

    @Test
    void customerAlreadyExistsException_message() {
        String cpf = "12345678901";
        CustomerAlreadyExistsException ex = new CustomerAlreadyExistsException(cpf);
        assertTrue(ex.getMessage().contains(cpf));
    }

    @Test
    void emailAlreadyExistsException_message() {
        String email = "email@test.com";
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException(email);
        assertTrue(ex.getMessage().contains(email));
    }

    @Test
    void invalidCpfException_message() {
        String cpf = "00000000000";
        InvalidCpfException ex = new InvalidCpfException(cpf);
        assertTrue(ex.getMessage().contains(cpf));
    }
}

