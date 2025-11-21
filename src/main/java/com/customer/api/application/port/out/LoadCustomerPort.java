package com.customer.api.application.port.out;

import com.customer.api.domain.Customer;
import com.customer.api.domain.CustomerId;
import com.customer.api.domain.Cpf;

import java.util.Optional;

public interface LoadCustomerPort {
    Optional<Customer> findById(CustomerId id);
    Optional<Customer> findByCpf(Cpf cpf);
    boolean existsByCpf(Cpf cpf);
    boolean existsByEmail(String email);
}