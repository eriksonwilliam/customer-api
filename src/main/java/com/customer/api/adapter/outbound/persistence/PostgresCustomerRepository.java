package com.customer.api.adapter.outbound.persistence;

import com.customer.api.application.port.out.*;
import com.customer.api.domain.Cpf;
import com.customer.api.domain.Customer;
import com.customer.api.domain.CustomerId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("!dev")
class PostgresCustomerRepository implements SaveCustomerPort, LoadCustomerPort,
        UpdateCustomerPort, DeleteCustomerPort, LoadCustomerPagePort {

    private final CustomerJpaRepository jpa;
    private final CustomerMapper mapper;

    PostgresCustomerRepository(CustomerJpaRepository jpa, CustomerMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override public void save(Customer customer) { jpa.save(mapper.toEntity(customer)); }
    @Override public void update(Customer customer) { jpa.save(mapper.toEntity(customer)); }
    @Override public void delete(Customer customer) { jpa.save(mapper.toEntity(customer)); }

    @Override public Optional<Customer> findById(CustomerId id) {
        return jpa.findById(id.value()).map(mapper::toDomain);
    }

    @Override public Optional<Customer> findByCpf(Cpf cpf) {
        return jpa.findByCpf(cpf.value()).map(mapper::toDomain);
    }

    @Override public boolean existsByCpf(Cpf cpf) { return jpa.existsByCpf(cpf.value()); }

    @Override
    public Page<Customer> findAll(String search, Pageable pageable) {
        return jpa.findActiveCustomers(search, pageable).map(mapper::toDomain);
    }
}