package com.lxp.common.application.cqrs;

/**
 * Query Bus 인터페이스
 * Query를 적절한 Handler로 라우팅
 */
public interface QueryBus {

    /**
     * Query 전송 후 결과 반환
     */
    <Q extends Query<R>, R> R dispatch(Q query);
}
