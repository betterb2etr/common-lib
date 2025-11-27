package com.lxp.common.application.event;

import java.time.LocalDateTime;

/**
 * 통합 이벤트 인터페이스
 * 바운디드 컨텍스트 간 통신에 사용
 */
public interface IntegrationEvent {

    String getEventId();

    LocalDateTime getOccurredAt();

    String getEventType();

    String getSource();

    default int getVersion() {
        return 1;
    }

    String getCorrelationId();

    default String getCausationId() {
        return null;
    }
}
