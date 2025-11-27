package com.lxp.common.domain.event;

import java.time.LocalDateTime;

/**
 * 바운디드 컨텍스트 내부에서 사용
 */
public interface DomainEvent {

    String getEventId();

    LocalDateTime getOccurredAt();

    default String getEventType() {
        return this.getClass().getSimpleName();
    }

    String getAggregateId();
}
