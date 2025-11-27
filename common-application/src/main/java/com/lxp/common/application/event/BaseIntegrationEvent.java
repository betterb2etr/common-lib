package com.lxp.common.application.event;

import com.lxp.common.domain.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 통합 이벤트 기본 구현
 * 외부 바운디드 컨텍스트 간 통신용
 */
public abstract class BaseIntegrationEvent implements IntegrationEvent {

    private final String eventId;
    private final LocalDateTime occurredAt;
    private final String source;
    private final String correlationId;
    private final String causationId;
    private final int version;

    protected BaseIntegrationEvent(String source) {
        this(source, UUID.randomUUID().toString(), null, 1);
    }

    protected BaseIntegrationEvent(String source, String correlationId) {
        this(source, correlationId, null, 1);
    }

    protected BaseIntegrationEvent(String source, String correlationId, String causationId, int version) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
        this.source = source;
        this.correlationId = correlationId;
        this.causationId = causationId;
        this.version = version;
    }

    protected BaseIntegrationEvent(String eventId, LocalDateTime occurredAt, String source,
                                    String correlationId, String causationId, int version) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.source = source;
        this.correlationId = correlationId;
        this.causationId = causationId;
        this.version = version;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getCorrelationId() {
        return correlationId;
    }

    @Override
    public String getCausationId() {
        return causationId;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getEventType() {
        return this.getClass().getSimpleName();
    }

    /**
     * DomainEvent로부터 IntegrationEvent 생성 시 사용
     */
    public static String correlationIdFrom(DomainEvent domainEvent) {
        return domainEvent.getEventId();
    }

    @Override
    public String toString() {
        return String.format("%s[eventId=%s, source=%s, correlationId=%s, version=%d, occurredAt=%s]",
                getEventType(), eventId, source, correlationId, version, occurredAt);
    }
}
