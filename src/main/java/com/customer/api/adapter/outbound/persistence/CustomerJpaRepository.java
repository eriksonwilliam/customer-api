package com.customer.api.adapter.outbound.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    Optional<CustomerEntity> findByCpf(String cpf);

    @Query("SELECT c FROM CustomerEntity c WHERE c.active = true " +
            "AND (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) )")
    Page<CustomerEntity> findActiveCustomers(String search, Pageable pageable);
}

