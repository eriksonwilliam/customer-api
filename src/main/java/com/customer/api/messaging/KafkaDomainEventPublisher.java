package com.customer.api.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "messaging.enabled", havingValue = "true", matchIfMissing = true)
class KafkaDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);
    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;
    private final String topic;

    KafkaDomainEventPublisher(KafkaTemplate<String, CustomerEvent> kafkaTemplate,
                              @Value("${messaging.customer.topic:customer.events.v1}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(CustomerEvent event) {
        kafkaTemplate.send(topic, event.id(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event type={} id={}: {}", event.type(), event.id(), ex.getMessage(), ex);
                    } else if (result != null) {
                        log.debug("Published event type={} id={} offset={} partition={}", event.type(), event.id(), result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
                    }
                });
    }
}
