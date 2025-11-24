package com.customer.api.messaging;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@Primary
@ConditionalOnProperty(name = "messaging.enabled", havingValue = "true", matchIfMissing = true)
class CompositeDomainEventPublisher implements DomainEventPublisher {
    private final List<DomainEventPublisher> delegates;

    CompositeDomainEventPublisher(List<DomainEventPublisher> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void publish(CustomerEvent event) {
        for (DomainEventPublisher delegate : delegates) {
            if (delegate == null || delegate == this) continue;
            try {
                delegate.publish(event);
            } catch (Exception e) {
                System.err.println("Erro ao publicar evento em " + delegate + ": " + e.getMessage());
            }
        }
    }
}
