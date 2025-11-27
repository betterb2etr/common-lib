package com.lxp.common.application.cqrs;

/**
 * 결과를 반환하는 Command 핸들러 인터페이스
 *
 * @param <C> Command 타입
 * @param <R> 결과 타입
 */
public interface CommandWithResultHandler<C extends Command, R> {
    
    /**
     * Command 처리 후 결과 반환
     */
    R handle(C command);
}
