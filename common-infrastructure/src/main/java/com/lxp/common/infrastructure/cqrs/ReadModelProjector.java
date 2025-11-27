package com.lxp.common.infrastructure.cqrs;

import com.lxp.common.domain.event.DomainEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Read Model Projector
 * 도메인 이벤트를 수신하여 적절한 ReadModelUpdater로 라우팅
 */
@Component
public class ReadModelProjector {

    private final Map<Class<?>, ReadModelUpdater<?>> updaterMap = new ConcurrentHashMap<>();

    public ReadModelProjector(List<ReadModelUpdater<?>> updaters) {
        updaters.forEach(updater -> 
                updaterMap.put(updater.supportedEventType(), updater));
    }

    /**
     * 도메인 이벤트 수신 및 처리
     * 비동기로 처리하여 Command 트랜잭션에 영향 없음
     */
    @Async
    @EventListener
    @SuppressWarnings("unchecked")
    public void onDomainEvent(DomainEvent event) {
        ReadModelUpdater<DomainEvent> updater = 
                (ReadModelUpdater<DomainEvent>) updaterMap.get(event.getClass());
        
        if (updater != null) {
            updater.update(event);
        }
    }
}
