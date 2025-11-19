package com.customer.api.domain.exception;

public class CustomerAlreadyExistsException extends DomainException {

    public CustomerAlreadyExistsException(String cpf) {
        super("Já existe cliente cadastrado com o CPF: " + cpf);
    }
}