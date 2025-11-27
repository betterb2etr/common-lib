package com.lxp.common.application.port.in;

import com.lxp.common.application.event.IntegrationEvent;

/**
 * 통합 이벤트 핸들러 인터페이스
 * 외부 바운디드 컨텍스트로부터 이벤트 수신
 *
 * @param <E> 처리할 이벤트 타입
 */
public interface IntegrationEventHandler<E extends IntegrationEvent> {

    /**
     * 이벤트 처리
     */
    void handle(E event);

    /**
     * 처리 가능한 이벤트 타입
     */
    Class<E> supportedEventType();
}
