package com.lxp.common.application.cqrs;

/**
 * Query 핸들러 인터페이스
 *
 * @param <Q> Query 타입
 * @param <R> 결과 타입
 */
public interface QueryHandler<Q extends Query<R>, R> {
    
    /**
     * Query 처리 후 결과 반환
     */
    R handle(Q query);
}
