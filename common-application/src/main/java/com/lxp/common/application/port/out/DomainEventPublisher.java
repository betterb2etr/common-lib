package com.lxp.common.application.port.out;

import com.lxp.common.domain.event.AggregateRoot;
import com.lxp.common.domain.event.DomainEvent;

import java.util.Collection;

/**
 * 도메인 이벤트 발행 포트
 * Infrastructure 계층에서 구현
 */
public interface DomainEventPublisher {

    /**
     * 단일 이벤트 발행
     */
    void publish(DomainEvent event);

    /**
     * 여러 이벤트 발행
     */
    default void publishAll(Collection<? extends DomainEvent> events) {
        events.forEach(this::publish);
    }

    /**
     * Aggregate의 모든 이벤트 발행 후 초기화
     */
    default void publishAndClear(AggregateRoot aggregate) {
        publishAll(aggregate.getDomainEvents());
        aggregate.clearDomainEvents();
    }

    /**
     * 여러 Aggregate의 모든 이벤트 발행 후 초기화
     */
    default void publishAndClearAll(Collection<? extends AggregateRoot> aggregates) {
        aggregates.forEach(this::publishAndClear);
    }
}
