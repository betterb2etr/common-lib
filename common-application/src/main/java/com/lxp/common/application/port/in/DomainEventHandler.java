package com.lxp.common.application.port.in;

import com.lxp.common.domain.event.DomainEvent;

/**
 * 도메인 이벤트 핸들러 인터페이스
 *
 * @param <E> 처리할 이벤트 타입
 */
public interface DomainEventHandler<E extends DomainEvent> {

    /**
     * 이벤트 처리
     */
    void handle(E event);

    /**
     * 처리 가능한 이벤트 타입
     */
    Class<E> supportedEventType();
}
