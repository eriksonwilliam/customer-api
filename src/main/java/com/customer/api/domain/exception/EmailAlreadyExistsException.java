package com.customer.api.domain.exception;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(String email) {
        super("Já existe cliente cadastrado com o email: " + email);
    }
}

