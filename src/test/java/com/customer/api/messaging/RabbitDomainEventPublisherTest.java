package com.customer.api.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.time.Instant;
import java.util.UUID;
import static org.mockito.Mockito.*;

class RabbitDomainEventPublisherTest {
    @Test
    void publish_sendsToCorrectRoutingKey() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitDomainEventPublisher publisher = new RabbitDomainEventPublisher(
                rabbitTemplate, "exchange", "created", "updated", "deleted");
        CustomerEvent created = new CustomerEvent(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            DomainEventType.CUSTOMER_CREATED, Instant.now(), 1,
            new CustomerEvent.Payload("name", "12345678901", "email@test.com", "11999999999", false)
        );
        CustomerEvent updated = new CustomerEvent(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            DomainEventType.CUSTOMER_UPDATED, Instant.now(), 1,
            new CustomerEvent.Payload("name", "12345678901", "email@test.com", "11999999999", false)
        );
        CustomerEvent deleted = new CustomerEvent(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            DomainEventType.CUSTOMER_DELETED, Instant.now(), 1,
            new CustomerEvent.Payload("name", "12345678901", "email@test.com", "11999999999", true)
        );
        publisher.publish(created);
        publisher.publish(updated);
        publisher.publish(deleted);
        verify(rabbitTemplate).convertAndSend(eq("exchange"), eq("created"), eq(created));
        verify(rabbitTemplate).convertAndSend(eq("exchange"), eq("updated"), eq(updated));
        verify(rabbitTemplate).convertAndSend(eq("exchange"), eq("deleted"), eq(deleted));
    }

    @Test
    void publish_handlesException() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        doThrow(new RuntimeException("fail")).when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(CustomerEvent.class));
        RabbitDomainEventPublisher publisher = new RabbitDomainEventPublisher(
                rabbitTemplate, "exchange", "created", "updated", "deleted");
        CustomerEvent event = new CustomerEvent(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            DomainEventType.CUSTOMER_CREATED, Instant.now(), 1,
            new CustomerEvent.Payload("name", "12345678901", "email@test.com", "11999999999", false)
        );
        publisher.publish(event);
    }
}
