package com.lxp.common.infrastructure.cqrs;

import com.lxp.common.application.cqrs.Query;
import com.lxp.common.application.cqrs.QueryBus;
import com.lxp.common.application.cqrs.QueryHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring 기반 Query Bus 구현체
 */
public class SimpleQueryBus implements QueryBus {

    private final ApplicationContext applicationContext;
    private final Map<Class<?>, QueryHandler<?, ?>> handlerCache = new ConcurrentHashMap<>();

    public SimpleQueryBus(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public <Q extends Query<R>, R> R dispatch(Q query) {
        QueryHandler<Q, R> handler = (QueryHandler<Q, R>) handlerCache.computeIfAbsent(
                query.getClass(),
                this::findHandler
        );
        return handler.handle(query);
    }

    @SuppressWarnings("rawtypes")
    private QueryHandler<?, ?> findHandler(Class<?> queryClass) {
        Map<String, QueryHandler> handlers = applicationContext.getBeansOfType(QueryHandler.class);

        return handlers.values().stream()
                .filter(handler -> {
                    Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(
                            handler.getClass(), QueryHandler.class);
                    return typeArgs != null && typeArgs[0].equals(queryClass);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No handler found for query: " + queryClass.getName()));
    }
}
