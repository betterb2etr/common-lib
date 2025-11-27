package com.lxp.common.application.cqrs;

/**
 * Command 핸들러 인터페이스
 *
 * @param <C> Command 타입
 */
public interface CommandHandler<C extends Command> {
    
    /**
     * Command 처리
     */
    void handle(C command);
}
