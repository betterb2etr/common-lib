package com.lxp.common.application.port.in;

/**
 * Query UseCase (R 작업)
 * CQRS의 Query 측 처리용
 */
public interface QueryUseCase<I, O> {
    O execute(I query);
}
