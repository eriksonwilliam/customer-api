package com.customer.api.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class KafkaDomainEventPublisherTest {
    @Test
    void publish_sendsToKafka() {
        KafkaTemplate<String, CustomerEvent> kafkaTemplate = mock(KafkaTemplate.class);
        CompletableFuture<SendResult<String, CustomerEvent>> future = new CompletableFuture<>();
        future.complete(null);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
        KafkaDomainEventPublisher publisher = new KafkaDomainEventPublisher(kafkaTemplate, "topic");
        CustomerEvent event = new CustomerEvent(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            DomainEventType.CUSTOMER_CREATED, Instant.now(), 1,
            new CustomerEvent.Payload("name", "12345678901", "email@test.com", "11999999999", false)
        );
        publisher.publish(event);
        verify(kafkaTemplate).send("topic", event.id(), event);
    }

    @Test
    void publish_handlesException() {
        KafkaTemplate<String, CustomerEvent> kafkaTemplate = mock(KafkaTemplate.class);
        CompletableFuture<SendResult<String, CustomerEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("fail"));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
        KafkaDomainEventPublisher publisher = new KafkaDomainEventPublisher(kafkaTemplate, "topic");
        CustomerEvent event = new CustomerEvent(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            DomainEventType.CUSTOMER_CREATED, Instant.now(), 1,
            new CustomerEvent.Payload("name", "12345678901", "email@test.com", "11999999999", false)
        );
        publisher.publish(event);
    }
}
