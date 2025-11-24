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

    @Test
    void publish_emptyPublisherList_doesNothing() {
        CompositeDomainEventPublisher composite = new CompositeDomainEventPublisher(List.of());
        CustomerEvent event = buildEvent();
        composite.publish(event);
    }

    @Test
    void publish_nullPublisher_ignored() {
        DomainEventPublisher publisher1 = mock(DomainEventPublisher.class);
        List<DomainEventPublisher> publishers = new java.util.ArrayList<>();
        publishers.add(publisher1);
        publishers.add(null);
        CompositeDomainEventPublisher composite = new CompositeDomainEventPublisher(publishers);
        CustomerEvent event = buildEvent();
        composite.publish(event);
        verify(publisher1).publish(event);
    }

    @Test
    void publish_multipleEvents_allDelegated() {
        DomainEventPublisher publisher1 = mock(DomainEventPublisher.class);
        DomainEventPublisher publisher2 = mock(DomainEventPublisher.class);
        CompositeDomainEventPublisher composite = new CompositeDomainEventPublisher(List.of(publisher1, publisher2));
        CustomerEvent event1 = buildEvent();
        CustomerEvent event2 = buildEvent();
        composite.publish(event1);
        composite.publish(event2);
        verify(publisher1).publish(event1);
        verify(publisher2).publish(event1);
        verify(publisher1).publish(event2);
        verify(publisher2).publish(event2);
    }

    @Test
    void publish_publisherThrowsException_othersStillCalled() {
        DomainEventPublisher publisher1 = mock(DomainEventPublisher.class);
        DomainEventPublisher publisher2 = mock(DomainEventPublisher.class);
        doThrow(new RuntimeException("fail")).when(publisher1).publish(any());
        CompositeDomainEventPublisher composite = new CompositeDomainEventPublisher(List.of(publisher1, publisher2));
        CustomerEvent event = buildEvent();
        composite.publish(event);
        verify(publisher1).publish(event);
        verify(publisher2).publish(event);
    }
}
