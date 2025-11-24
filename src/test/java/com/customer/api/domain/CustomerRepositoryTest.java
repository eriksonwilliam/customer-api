package com.customer.api.domain;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryCustomerRepository implements CustomerRepository {
    private final HashMap<CustomerId, Customer> map = new HashMap<>();
    @Override
    public void save(Customer customer) { map.put(customer.id(), customer); }
    @Override
    public Optional<Customer> findById(CustomerId id) { return Optional.ofNullable(map.get(id)); }
    @Override
    public Optional<Customer> findByCpf(Cpf cpf) {
        return map.values().stream().filter(c -> c.cpf().equals(cpf)).findFirst();
    }
    @Override
    public boolean existsByCpf(Cpf cpf) {
        return map.values().stream().anyMatch(c -> c.cpf().equals(cpf));
    }
}

class CustomerRepositoryTest {
    @Test
    void saveAndFind() {
        CustomerRepository repo = new InMemoryCustomerRepository();
        CustomerId id = CustomerId.generate();
        Cpf cpf = new Cpf("52998224725");
        Customer customer = new Customer(id, "Joao", cpf, "joao@email.com", "123456789", java.time.LocalDateTime.now());
        repo.save(customer);
        assertTrue(repo.findById(id).isPresent());
        assertTrue(repo.findByCpf(cpf).isPresent());
        assertTrue(repo.existsByCpf(cpf));
    }
    @Test
    void notFound() {
        CustomerRepository repo = new InMemoryCustomerRepository();
        CustomerId id = CustomerId.generate();
        Cpf cpf = new Cpf("52998224725");
        assertFalse(repo.findById(id).isPresent());
        assertFalse(repo.findByCpf(cpf).isPresent());
        assertFalse(repo.existsByCpf(cpf));
    }
}

