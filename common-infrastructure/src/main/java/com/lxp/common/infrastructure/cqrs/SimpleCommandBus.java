package com.lxp.common.infrastructure.cqrs;

import com.lxp.common.application.cqrs.Command;
import com.lxp.common.application.cqrs.CommandBus;
import com.lxp.common.application.cqrs.CommandHandler;
import com.lxp.common.application.cqrs.CommandWithResultHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring 기반 Command Bus 구현체
 */
@Component
public class SimpleCommandBus implements CommandBus {

    private final ApplicationContext applicationContext;
    private final Map<Class<?>, CommandHandler<?>> handlerCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, CommandWithResultHandler<?, ?>> resultHandlerCache = new ConcurrentHashMap<>();

    public SimpleCommandBus(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public <C extends Command> void dispatch(C command) {
        CommandHandler<C> handler = (CommandHandler<C>) handlerCache.computeIfAbsent(
                command.getClass(),
                this::findHandler
        );
        handler.handle(command);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public <C extends Command, R> R dispatchWithResult(C command) {
        CommandWithResultHandler<C, R> handler = (CommandWithResultHandler<C, R>) resultHandlerCache.computeIfAbsent(
                command.getClass(),
                this::findResultHandler
        );
        return handler.handle(command);
    }

    @SuppressWarnings("rawtypes")
    private CommandHandler<?> findHandler(Class<?> commandClass) {
        Map<String, CommandHandler> handlers = applicationContext.getBeansOfType(CommandHandler.class);

        return handlers.values().stream()
                .filter(handler -> {
                    Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(
                            handler.getClass(), CommandHandler.class);
                    return typeArgs != null && typeArgs[0].equals(commandClass);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No handler found for command: " + commandClass.getName()));
    }

    @SuppressWarnings("rawtypes")
    private CommandWithResultHandler<?, ?> findResultHandler(Class<?> commandClass) {
        Map<String, CommandWithResultHandler> handlers = applicationContext.getBeansOfType(CommandWithResultHandler.class);

        return handlers.values().stream()
                .filter(handler -> {
                    Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(
                            handler.getClass(), CommandWithResultHandler.class);
                    return typeArgs != null && typeArgs[0].equals(commandClass);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No result handler found for command: " + commandClass.getName()));
    }
}
