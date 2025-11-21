package com.customer.api.adapter.outbound.persistence;

import com.customer.api.application.port.out.*;
import com.customer.api.domain.Cpf;
import com.customer.api.domain.Customer;
import com.customer.api.domain.CustomerId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

@Repository
@Profile("!dev")
class PostgresCustomerRepository implements SaveCustomerPort, LoadCustomerPort,
        UpdateCustomerPort, DeleteCustomerPort, LoadCustomerPagePort {

    private final CustomerJpaRepository jpa;
    private final CustomerMapper mapper;
    private final EntityManager em;

    PostgresCustomerRepository(CustomerJpaRepository jpa, CustomerMapper mapper, EntityManager em) {
        this.jpa = jpa;
        this.mapper = mapper;
        this.em = em;
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
    @Override public boolean existsByEmail(String email) { return jpa.existsByEmail(email); }

    @Override
    public Page<Customer> findAll(String search, Pageable pageable) {
        if (search != null) {
            search = search.isBlank() ? null : search;
        }
        String baseWhere = "c.active = true";
        String filter = (search == null) ? baseWhere : baseWhere + " AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))";
        String select = "SELECT c FROM CustomerEntity c WHERE " + filter;
        String count = "SELECT COUNT(c) FROM CustomerEntity c WHERE " + filter;
        TypedQuery<CustomerEntity> query = em.createQuery(select, CustomerEntity.class);
        TypedQuery<Long> countQuery = em.createQuery(count, Long.class);
        if (search != null) {
            query.setParameter("search", search);
            countQuery.setParameter("search", search);
        }
        long total = countQuery.getSingleResult();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        var list = query.getResultList().stream().map(mapper::toDomain).toList();
        return new org.springframework.data.domain.PageImpl<>(list, pageable, total);
    }
}