package com.customer.api.domain;

import java.util.Optional;

public interface CustomerRepository {

    void save(Customer customer);

    Optional<Customer> findById(CustomerId id);

    Optional<Customer> findByCpf(Cpf cpf);

    boolean existsByCpf(Cpf cpf);
}