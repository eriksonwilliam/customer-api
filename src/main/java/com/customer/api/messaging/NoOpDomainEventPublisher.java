package com.customer.api.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@ConditionalOnProperty(name = "messaging.enabled", havingValue = "false")
class NoOpDomainEventPublisher implements DomainEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(NoOpDomainEventPublisher.class);
    @Override
    public void publish(CustomerEvent event) {
        log.debug("[NO-OP] Mensageria desabilitada. Evento ignorado type={} id={}", event.type(), event.id());
    }
}

