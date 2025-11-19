package com.customer.api.domain.exception;

import java.util.UUID;

public class CustomerNotFoundException extends DomainException {
    public CustomerNotFoundException(UUID id) {
        super("Cliente não encontrado com o ID: " + id);
    }
}