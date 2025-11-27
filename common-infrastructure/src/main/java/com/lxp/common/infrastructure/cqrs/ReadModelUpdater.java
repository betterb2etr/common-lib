package com.lxp.common.infrastructure.cqrs;

import com.lxp.common.domain.event.DomainEvent;

/**
 * CQRS Read Model 업데이터 인터페이스
 * 도메인 이벤트 수신하여 Read Model 갱신
 *
 * @param <E> 도메인 이벤트 타입
 */
public interface ReadModelUpdater<E extends DomainEvent> {

    /**
     * 이벤트 수신하여 Read Model 갱신
     */
    void update(E event);

    /**
     * 처리 가능한 이벤트 타입
     */
    Class<E> supportedEventType();
}
