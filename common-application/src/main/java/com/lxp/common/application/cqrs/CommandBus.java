package com.lxp.common.application.cqrs;

/**
 * Command Bus 인터페이스
 * Command를 적절한 Handler로 라우팅
 */
public interface CommandBus {

    /**
     * Command 전송 (결과 없음)
     */
    <C extends Command> void dispatch(C command);

    /**
     * Command 전송 (결과 반환)
     */
    <C extends Command, R> R dispatchWithResult(C command);
}
