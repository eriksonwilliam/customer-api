package com.customer.api.messaging;

public interface DomainEventPublisher {
    void publish(CustomerEvent event);
}

