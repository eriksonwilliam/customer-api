package com.customer.api.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@ConditionalOnProperty(name = "messaging.rabbit.enabled", havingValue = "true")
class RabbitDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitDomainEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String createdKey;
    private final String updatedKey;
    private final String deletedKey;

    RabbitDomainEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${messaging.rabbit.exchange:customer.events}") String exchange,
                               @Value("${messaging.rabbit.routing.created:customer.created}") String createdKey,
                               @Value("${messaging.rabbit.routing.updated:customer.updated}") String updatedKey,
                               @Value("${messaging.rabbit.routing.deleted:customer.deleted}") String deletedKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.createdKey = createdKey;
        this.updatedKey = updatedKey;
        this.deletedKey = deletedKey;
    }

    @Override
    public void publish(CustomerEvent event) {
        String routingKey = switch (event.type()) {
            case CUSTOMER_CREATED -> createdKey;
            case CUSTOMER_UPDATED -> updatedKey;
            case CUSTOMER_DELETED -> deletedKey;
        };
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.debug("Published Rabbit event type={} id={} rk={}", event.type(), event.id(), routingKey);
        } catch (Exception ex) {
            log.error("Failed to publish Rabbit event type={} id={}: {}", event.type(), event.id(), ex.getMessage(), ex);
        }
    }
}
