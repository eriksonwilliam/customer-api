package com.customer.api.messaging;

import com.customer.api.domain.Customer;

import java.time.Instant;
import java.util.UUID;

public record CustomerEvent(
        String eventId,
        String correlationId,
        String id,
        DomainEventType type,
        Instant occurredAt,
        int version,
        Payload payload
) {
    public static final int CURRENT_VERSION = 1;

    public static CustomerEvent created(Customer customer) {
        return build(customer, DomainEventType.CUSTOMER_CREATED, false);
    }

    public static CustomerEvent updated(Customer customer) {
        return build(customer, DomainEventType.CUSTOMER_UPDATED, false);
    }

    public static CustomerEvent deleted(Customer customer) {
        return build(customer, DomainEventType.CUSTOMER_DELETED, true);
    }

    private static CustomerEvent build(Customer customer, DomainEventType type, boolean deleted) {
        String eventId = UUID.randomUUID().toString();
        String correlationId = eventId;
        return new CustomerEvent(
                eventId,
                correlationId,
                customer.id().value().toString(),
                type,
                Instant.now(),
                CURRENT_VERSION,
                new Payload(
                        customer.name(),
                        customer.cpf().value(),
                        customer.email(),
                        customer.phone(),
                        deleted
                )
        );
    }

    public record Payload(
            String name,
            String cpf,
            String email,
            String phone,
            boolean deleted
    ) {}
}
