package com.customer.api.messaging;

import static org.mockito.Mockito.*;
import java.util.List;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CompositeDomainEventPublisherTest {
    private CustomerEvent buildEvent() {
        return new CustomerEvent(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            DomainEventType.CUSTOMER_CREATED,
            Instant.now(),
            1,
            new CustomerEvent.Payload("name", "12345678901", "email@test.com", "11999999999", false)
        );
    }

    @Test
    void publish_delegatesToOtherPublishers() {
        DomainEventPublisher publisher1 = mock(DomainEventPublisher.class);
        DomainEventPublisher publisher2 = mock(DomainEventPublisher.class);
        CompositeDomainEventPublisher composite = new CompositeDomainEventPublisher(List.of(publisher1, publisher2));
        CustomerEvent event = buildEvent();
        composite.publish(event);
        verify(publisher1).publish(event);
        verify(publisher2).publish(event);
    }

    @Test
    void publish_doesNotDelegateToSelf() {
        CompositeDomainEventPublisher composite = new CompositeDomainEventPublisher(List.of());
        CustomerEvent event = buildEvent();
        composite.publish(event);
    }
}
