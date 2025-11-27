package com.lxp.common.application.port.out;

import com.lxp.common.application.event.IntegrationEvent;

/**
 * 통합 이벤트 발행 포트
 * 외부 바운디드 컨텍스트로 이벤트 전송
 */
public interface IntegrationEventPublisher {

    /**
     * 통합 이벤트 발행
     */
    void publish(IntegrationEvent event);

    /**
     * 토픽 지정하여 발행
     */
    void publish(String topic, IntegrationEvent event);
}
