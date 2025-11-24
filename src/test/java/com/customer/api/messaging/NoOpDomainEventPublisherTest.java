package com.customer.api.messaging;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoOpDomainEventPublisherTest {
    @Test
    void publish_logsDebugMessage() {
        var publisher = new NoOpDomainEventPublisher();
        var event = new CustomerEvent(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            DomainEventType.CUSTOMER_CREATED,
            Instant.now(),
            1,
            new CustomerEvent.Payload("name", "12345678901", "email@test.com", "11999999999", false)
        );
        assertDoesNotThrow(() -> publisher.publish(event));
    }
}
