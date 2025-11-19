package com.customer.api.adapter.outbound.persistence;

import com.customer.api.domain.*;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toDomain(CustomerEntity entity) {
        return new Customer(
                new CustomerId(entity.getId()),
                entity.getName(),
                new Cpf(entity.getCpf()),
                entity.getEmail(),
                entity.getPhone(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive(),
                entity.getDeletedAt()
        );
    }

    public CustomerEntity toEntity(Customer domain) {
        CustomerEntity entity = new CustomerEntity(
                domain.id().value(),
                domain.name(),
                domain.cpf().value(),
                domain.email(),
                domain.phone(),
                domain.createdAt()
        );
        entity.setUpdatedAt(domain.updatedAt());
        entity.setActive(domain.active());
        entity.setDeletedAt(domain.deletedAt());
        return entity;
    }
}