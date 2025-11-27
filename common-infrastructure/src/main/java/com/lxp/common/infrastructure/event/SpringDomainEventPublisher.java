package com.lxp.common.infrastructure.event;

import com.lxp.common.application.port.out.DomainEventPublisher;
import com.lxp.common.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Spring ApplicationEventPublisher를 사용한 도메인 이벤트 발행 구현체
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Domain event must not be null");
        }
        applicationEventPublisher.publishEvent(event);
    }
}
