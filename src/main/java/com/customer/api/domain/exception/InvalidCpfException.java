package com.customer.api.domain.exception;

public class InvalidCpfException extends DomainException {

    public InvalidCpfException(String cpf) {
        super("CPF inválido: " + cpf);
    }
}